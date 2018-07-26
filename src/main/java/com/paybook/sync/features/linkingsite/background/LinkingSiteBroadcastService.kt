package com.paybook.sync.features.linkingsite.background

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import com.google.gson.Gson
import com.paybook.core.BaseService
import com.paybook.core.exception.OnErrorNotImplementedException
import com.paybook.sync.AddAccountRxWebSocket
import com.paybook.sync.AddAccountRxWebSocket.SocketEvent
import com.paybook.sync.AddAccountRxWebSocket.SocketEventType.END
import com.paybook.sync.AddAccountWebSocketResponse
import com.paybook.sync.SyncModule
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.LinkingSiteEventType
import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import com.paybook.sync.entities.twofa.TwoFaCredential
import com.paybook.sync.entities.twofa.TwoFaImage
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import okhttp3.OkHttpClient
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.lang.ref.WeakReference
import java.net.SocketTimeoutException
import java.util.ArrayList
import java.util.concurrent.TimeUnit

/**
 * Created by Gerardo Teruel on 3/27/18.
 */

class LinkingSiteBroadcastService : BaseService() {

  private var disposable: CompositeDisposable? = null

  override fun inject() {
    disposable = CompositeDisposable()
  }

  override fun onDestroy() {
    disposable!!.dispose()
    super.onDestroy()
  }

  override fun onStartCommand(
    intent: Intent,
    flags: Int,
    startId: Int
  ): Int {
    val webSocket = intent.getStringExtra(IK_SOCKET_URL)
    val organization = intent.getSerializableExtra(IK_ORGANIZATION) as Organization
    val site = intent.getSerializableExtra(IK_SITE) as Site
    val jobId = intent.getStringExtra(IK_JOB_ID)
    val schedulerProvider = SyncModule.scheduler

    val gson = Gson()
    val client = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS)
        .build()
    val imageConverter = TwoFaImageConverter(jobId, this)

    val d = AddAccountRxWebSocket.observable(client, webSocket)
        .onErrorReturn {
          if (it is SocketTimeoutException) {
            stopSelf(startId)
            SocketEvent(END, "")
          } else {
            throw IllegalStateException("Unexpected socket error", it)
          }
        }
        .observeOn(schedulerProvider.io())
        .doOnTerminate {
          imageConverter.clear()
          stopSelf(startId)
        }
        .filter { e -> e.type === AddAccountRxWebSocket.SocketEventType.MESSAGE }
        .map { e -> gson.fromJson(e.payload, AddAccountWebSocketResponse::class.java) }
        // We delay so that there's a window of time for the user to go back to the view before
        // a response is processed.
        .concatMap { e ->
          Observable.just(e)
              .delay(500, TimeUnit.MILLISECONDS)
        }
        .map { r ->
          linkingSite(
              organization, site, jobId, r as AddAccountWebSocketResponse, imageConverter
          )
        }
        .subscribe({ r ->
          val i = Intent(ACTION_LINKING_SITE_EVENT)
          i.putExtra(IK_DATA, r)
          sendOrderedBroadcast(i, PERMISSION)
        }) { e -> throw OnErrorNotImplementedException(e) }
    this.disposable!!.add(d)

    return Service.START_REDELIVER_INTENT
  }

  fun linkingSite(
    organization: Organization,
    site: Site,
    jobId: String,
    response: AddAccountWebSocketResponse,
    imageConverter: TwoFaImageConverter
  ): LinkingSiteEvent {
    val type = response.toEvent()
    var twoFaImages: List<TwoFaImage>? = null
    var twoFaCredentials: List<TwoFaCredential>? = null

    if (type === LinkingSiteEventType.TWO_FA) {
      twoFaCredentials = response.credentials
    } else if (type === LinkingSiteEventType.TWO_FA_IMAGES) {
      twoFaImages = imageConverter.convert(response.imageOptions!!)
    }

    return LinkingSiteEvent(
        organization, site, jobId, type, twoFaCredentials, twoFaImages
    )
  }

  class TwoFaImageConverter internal constructor(
    private val jobId: String,
    context: Context
  ) {
    private val context = WeakReference<Context>(context)

    fun convert(twoFaImages: List<AddAccountWebSocketResponse.TwoFaImageResponse>): List<TwoFaImage> {
      val values = ArrayList<TwoFaImage>()
      for (twoFaImage in twoFaImages) {
        val fileWithValue = if (!twoFaImage.isUrl) {
          store(twoFaImage)
          val uri = Uri.fromFile(File(filename(twoFaImage)))
              .toString()
          TwoFaImage(uri, twoFaImage.value)
        } else {
          TwoFaImage(twoFaImage.imgUrl!!, twoFaImage.value)
        }
        values.add(fileWithValue)
      }
      return values
    }

    private fun store(twoFaImage: AddAccountWebSocketResponse.TwoFaImageResponse) {
      try {
        context.get()!!
            .openFileOutput(filename(twoFaImage), Context.MODE_PRIVATE)
            .use { os ->
              val writer = PrintWriter(os)
              writer.print(twoFaImage.img)
            }
      } catch (e: IOException) {
        throw IllegalStateException("Couldn't write two fa image", e)
      }

    }

    private fun filename(twoFaImage: AddAccountWebSocketResponse.TwoFaImageResponse): String {
      return jobId + File.separator + IMAGE_PREFIX + twoFaImage.value.toString()
    }

    fun clear() {
      val dir = context.get()!!
          .getDir(jobId, Context.MODE_PRIVATE)
      for (file in dir.listFiles()) {
        file.delete()
      }
      dir.delete()
    }

    companion object {
      private const val IMAGE_PREFIX = "IMAGE_"
    }
  }

  companion object {
    private const val ACTION_LINKING_SITE_EVENT = "com.paybook.glass.linkingsite.event"
    private val PERMISSION = SyncModule.permission

    private const val IK_DATA = "com.paybook.glass.linkingsite.event.data"

    private const val IK_SOCKET_URL =
      "com.paybook.glass.features.addinstitution.linkingsite.websocketurl"
    private const val IK_ORGANIZATION =
      "com.paybook.glass.features.addinstitution.linkingsite.organization"
    private const val IK_SITE = "com.paybook.glass.features.addinstitution.linkingsite.site"
    private const val IK_JOB_ID = "com.paybook.glass.features.addinstitution.linkingsite.jobId"

    fun intentFilter(): IntentFilter {
      return IntentFilter(ACTION_LINKING_SITE_EVENT)
    }

    fun parse(data: Intent): LinkingSiteEvent {
      return data.getSerializableExtra(IK_DATA) as LinkingSiteEvent
    }

    fun permission(): String {
      return PERMISSION
    }

    fun newIntent(
      from: Context,
      socketUrl: String,
      organization: Organization,
      site: Site,
      jobId: String
    ): Intent {
      val i = Intent(from, LinkingSiteBroadcastService::class.java)
      i.putExtra(IK_SOCKET_URL, socketUrl)
      i.putExtra(IK_ORGANIZATION, organization)
      i.putExtra(IK_SITE, site)
      i.putExtra(IK_JOB_ID, jobId)
      return i
    }
  }
}

package com.paybook.sync.features.linkingsite

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationManagerCompat
import android.widget.ImageView
import com.evernote.android.state.State
import com.paybook.sync.R
import com.paybook.sync.SyncModule
import com.paybook.sync.base.BaseActivity
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.LinkingSiteEventType
import com.paybook.sync.features.linkingsite.background.LinkingSiteBroadcastService
import java.lang.ref.WeakReference

/**
 * Created by Gerardo Teruel on 3/26/18.
 */

class LinkingSiteActivity : BaseActivity(), LinkingSiteContract.View {

  companion object {
    private const val IK_DATA = "com.paybook.sync.linkingsite.data"
    private const val IK_EVENT = "com.paybook.sync.linkingsite.event"

    // Creational methods.
    fun newIntent(
      from: Context,
      linkingSiteData: LinkingSiteData
    ): Intent {
      val i = Intent(from, LinkingSiteActivity::class.java)
      i.putExtra(IK_DATA, linkingSiteData)
      return i
    }

    fun twoFaIntent(
      from: Context,
      event: LinkingSiteEvent
    ): Intent {
      val i = Intent(from, LinkingSiteActivity::class.java)
      i.putExtra(IK_EVENT, event)
      return i
    }
  }

  @State var data: LinkingSiteData? = null

  private lateinit var coverView: ImageView

  lateinit var presenter: LinkingSiteContract.Presenter

  private val broadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(
      context: Context,
      intent: Intent
    ) {
      val event = LinkingSiteBroadcastService.parse(intent)
      resultCode = Activity.RESULT_CANCELED
      onResponseReceived(event)
    }
  }

  // Lifecycle methods.
  override fun setView() {
    setContentView(R.layout.activity_linking_site)
    coverView = findViewById(R.id.imgCover)
  }

  override fun inject() {
    if (data == null && intent.hasExtra(IK_DATA)) {
      data = intent.getSerializableExtra(IK_DATA) as LinkingSiteData
    } else if (intent.hasExtra(IK_EVENT)) {
      val event = intent.getSerializableExtra(IK_EVENT) as LinkingSiteEvent
      data = LinkingSiteData(
          organization = event.organization,
          site = event.site,
          jobId = event.jobId
      )
      presenter.onEvent(event)
    } else {
      throw IllegalStateException("Unexpected launch without LinkingSiteData provided")
    }

    val navigator = LinkingSiteNavigator(this, applicationContext)
    val messages = LinkingSiteMessages(this)
    val repository = SyncModule.linkingSiteRepository
    presenter = LinkingSitePresenter(this, navigator, messages, repository)
  }

  override fun onNewIntent(intent: Intent) {
    val event = intent.getSerializableExtra(IK_EVENT) as LinkingSiteEvent
    presenter.onEvent(event)
    super.onNewIntent(intent)
  }

  override fun onViewCreated() {
    SyncModule.imageUrlLoaderFactory
        .create(data!!.organization.coverImageUrl)
        .load(coverView)
  }

  override fun onResume() {
    super.onResume()
    presenter.subscribe(data!!.jobId)
  }

  override fun onPause() {
    super.onPause()
    try {
      unregisterReceiver(broadcastReceiver)
    } catch (ignored: IllegalArgumentException) {
      // Ignore case when receiver was not registered.
    }

  }

  // Callbacks.
  fun onResponseReceived(event: LinkingSiteEvent) {
    val type = event.eventType
    when {
      type === LinkingSiteEventType.TWO_FA -> presenter.onTwoFa(event)
      type === LinkingSiteEventType.TWO_FA_IMAGES -> presenter.onTwoFaImages(event)
      else -> presenter.onEvent(event)
    }
  }

  // View methods
  override fun registerForLinkingSiteEvents() {
    val intentFilter = LinkingSiteBroadcastService.intentFilter()
    val permission = SyncModule.permission
    registerReceiver(broadcastReceiver, intentFilter, permission, null)
  }

  override fun hideNotification(jobId: String) {
    val notificationManagerCompat = NotificationManagerCompat.from(this)
    notificationManagerCompat.cancel(jobId.hashCode())
  }

  class LinkingSiteMessages(context: Context) : LinkingSiteContract.Messages {

    private val contextReference = WeakReference<Context>(context)

    override fun titleError(): String {
      return contextReference.get()!!
          .getString(R.string.screen_linking_site_title_error)
    }

    override fun descriptionAccountLocked(): String {
      return contextReference.get()!!
          .getString(R.string.screen_linking_site_description_account_locked)
    }

    override fun descriptionAlreadyLoggedIn(): String {
      return contextReference.get()!!
          .getString(R.string.screen_linking_description_logged_in)
    }

    override fun descriptionIncorrectCredentials(): String {
      return contextReference.get()!!
          .getString(R.string.screen_linking_site_description_incorrect_credentials)
    }

    override fun descriptionTimeOut(): String {
      return contextReference.get()!!
          .getString(R.string.screen_linking_site_description_timeout)
    }
  }
}

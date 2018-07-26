package com.paybook.sync.features.linkingsite

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationManagerCompat
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.evernote.android.state.State
import com.joanzapata.iconify.widget.IconTextView
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

    private const val IK_DATA = "com.paybook.glass.linkingsite.data"
    private const val IK_EVENT = "com.paybook.glass.linkingsite.event"

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

  private lateinit var loadingView: View
  private lateinit var iconBackground: ImageView
  private lateinit var iconView: IconTextView

  private lateinit var titleView: TextView
  private lateinit var descriptionView: TextView

  private lateinit var reAttemptView: Button
  private lateinit var goToHomeView: Button

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

    loadingView = findViewById(R.id.loadingIndicator)
    iconBackground = findViewById(R.id.circle)
    iconView = findViewById(R.id.icon)

    titleView = findViewById(R.id.txtTitle)
    descriptionView = findViewById(R.id.txtDescription)

    goToHomeView = findViewById(R.id.btnGoToHome)
    reAttemptView = findViewById(R.id.btnResync)
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
      presenter.onEvent(event.eventType)
    } else {
      throw IllegalStateException("Unexpected launch without LinkingSiteData provided")
    }

    goToHomeView.setOnClickListener { presenter.onGoToHome() }
    reAttemptView.setOnClickListener {
      presenter.onReattemptLink(
          data!!.site, data!!.organization
      )
    }

    val navigator = LinkingSiteNavigator(this, applicationContext)
    val messages = LinkingSiteMessages(this)
    val repository = SyncModule.linkingSiteRepository
    presenter = LinkingSitePresenter(this, navigator, messages, repository)
  }

  override fun onNewIntent(intent: Intent) {
    val event = intent.getSerializableExtra(IK_EVENT) as LinkingSiteEvent
    presenter.onEvent(event.eventType)
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
      else -> presenter.onEvent(type)
    }
  }

  // View methods
  override fun registerForLinkingSiteEvents() {
    val intentFilter = LinkingSiteBroadcastService.intentFilter()
    val permission = LinkingSiteBroadcastService.permission()
    registerReceiver(broadcastReceiver, intentFilter, permission, null)
  }

  @SuppressLint("SetTextI18n") override fun showAccountLinked() {
    loadingView.visibility = View.INVISIBLE
  }

  @SuppressLint("SetTextI18n")
  override fun showError(
    reason: String,
    description: String
  ) {
    loadingView.visibility = View.INVISIBLE

    iconView.text = "{fa-exclamation-triangle}"
    iconBackground.setImageResource(R.color.red_warning)

    reAttemptView.visibility = View.VISIBLE

    titleView.text = reason
    descriptionView.text = description
  }

  @SuppressLint("SetTextI18n") override fun showPaybookError() {
    loadingView.visibility = View.INVISIBLE

    iconView.text = "{fa-exclamation-triangle}"
    iconBackground.setImageResource(R.color.red_warning)

    goToHomeView.visibility = View.VISIBLE

    titleView.setText(R.string.screen_linking_site_title_server_error)
    descriptionView.setText(R.string.screen_linking_site_description_server_error)
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

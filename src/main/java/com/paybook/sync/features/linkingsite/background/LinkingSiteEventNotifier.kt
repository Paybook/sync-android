package com.paybook.sync.features.linkingsite.background

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import com.paybook.sync.R
import com.paybook.sync.SyncModule
import com.paybook.sync.features.linksite.LinkSiteActivity
import com.paybook.sync.features.linkingsite.twofa.twofaimages.TwoFaImagesFragment
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.LinkingSiteEventType
import com.paybook.sync.entities.LinkingSiteEventType.PROCESSING
import com.paybook.sync.entities.LinkingSiteEventType.SUCCESS
import com.paybook.sync.entities.LinkingSiteEventType.TWO_FA
import com.paybook.sync.entities.LinkingSiteEventType.TWO_FA_IMAGES
import com.paybook.sync.features.linkingsite.LinkingSiteActivity
import java.lang.ref.WeakReference

/**
 * Created by Gerardo Teruel on 3/27/18.
 */

class LinkingSiteEventNotifier(context: Context, private val event: LinkingSiteEvent) {

  companion object {
    private const val CHANNEL_ID = "ADD_ACCOUNT_CHANNEL"
  }

  private val contextWeakReference: WeakReference<Context> = WeakReference(context)
  private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
  private val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)

  fun sendNotification() {
    if (event.eventType == PROCESSING) {
      return
    }
    notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSmallIcon(SyncModule.notificationIcon)
        .setAutoCancel(true)
        .setContentTitle(title())
        .setContentText(description())
        .setContentIntent(pendingIntent())

    notificationManager.notify(event.jobId.hashCode(), notificationBuilder.build())
  }

  private fun title(): String {
    with(contextWeakReference.get()!!) {
      return when (event.eventType) {
        LinkingSiteEventType.PROCESSING -> throw IllegalStateException("No notification for processing")
        LinkingSiteEventType.SUCCESS -> getString(R.string.notification_title_success_link_account)
        LinkingSiteEventType.ACCOUNT_LOCKED -> getString(R.string.notification_title_error_link_account)
        LinkingSiteEventType.ALREADY_LOGGED_IN -> getString(R.string.notification_title_error_link_account)
        LinkingSiteEventType.INCORRECT_CREDENTIALS -> getString(R.string.notification_title_error_link_account)
        LinkingSiteEventType.TIMEOUT -> getString(R.string.notification_title_error_link_account)
        LinkingSiteEventType.TWO_FA -> getString(R.string.notification_title_progress_link_account)
        LinkingSiteEventType.TWO_FA_IMAGES -> getString(R.string.notification_title_progress_link_account)
        LinkingSiteEventType.SERVER_ERROR -> getString(R.string.notification_title_error_link_account)
      }.format(event.site.name)
    }
  }

  private fun description(): String {
    with(contextWeakReference.get()!!) {
      return when (event.eventType) {
        PROCESSING -> throw IllegalStateException("No notification for processing")
        LinkingSiteEventType.SUCCESS -> getString(R.string.notification_message_linked).format(event.site.name)
        LinkingSiteEventType.ACCOUNT_LOCKED -> getString(R.string.notification_message_account_locked)
        LinkingSiteEventType.ALREADY_LOGGED_IN -> getString(R.string.notification_message_logged_in)
        LinkingSiteEventType.INCORRECT_CREDENTIALS -> getString(R.string.notification_message_incorrect_credentials)
        LinkingSiteEventType.TIMEOUT -> getString(R.string.notification_message_timeout)
        LinkingSiteEventType.TWO_FA -> getString(R.string.notification_message_more_info)
        LinkingSiteEventType.TWO_FA_IMAGES -> getString(R.string.notification_message_more_info)
        LinkingSiteEventType.SERVER_ERROR -> getString(R.string.notification_message_server_busy)
      }
    }
  }

  private fun pendingIntent(): PendingIntent {
    val builder = TaskStackBuilder.create(contextWeakReference.get()!!)
    builder.addNextIntent(intent())
    return builder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)!!
  }

  private fun intent(): Intent {
    return when (event.eventType) {
      PROCESSING -> throw IllegalStateException("No notification for processing")
      TWO_FA -> LinkingSiteActivity.twoFaIntent(contextWeakReference.get()!!, event)
      TWO_FA_IMAGES -> LinkingSiteActivity.twoFaIntent(contextWeakReference.get()!!, event)
      SUCCESS -> SyncModule.homeIntentFactory.create(contextWeakReference.get()!!)
      else -> LinkSiteActivity.newIntent(contextWeakReference.get()!!, event.organization, event.site)
    }
  }

}

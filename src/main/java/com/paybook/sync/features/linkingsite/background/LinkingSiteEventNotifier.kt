package com.paybook.sync.features.linkingsite.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import android.util.Log
import com.paybook.sync.R
import com.paybook.sync.SyncModule
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.LinkingSiteEventType
import com.paybook.sync.entities.LinkingSiteEventType.PROCESSING
import com.paybook.sync.entities.LinkingSiteEventType.SUCCESS
import com.paybook.sync.entities.LinkingSiteEventType.TWO_FA
import com.paybook.sync.entities.LinkingSiteEventType.TWO_FA_IMAGES
import com.paybook.sync.features.linkingsite.LinkingSiteActivity
import com.paybook.sync.features.linksite.LinkSiteActivity
import java.lang.ref.WeakReference

/**
 * Created by Gerardo Teruel on 3/27/18.
 */

class LinkingSiteEventNotifier(
  context: Context,
  private val event: LinkingSiteEvent
) {

  companion object {
    private const val CHANNEL_ID = "ADD_ACCOUNT_CHANNEL"
  }

  private val contextRef: WeakReference<Context> = WeakReference(context)
  private val notificationManager: NotificationManagerCompat =
    NotificationManagerCompat.from(context)
  private val notificationBuilder: NotificationCompat.Builder =
    NotificationCompat.Builder(context, CHANNEL_ID)

  fun sendNotification() {
    if (event.eventType == PROCESSING) {
      return
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = contextRef.get()!!.getString(R.string.channel_name)
      val description = contextRef.get()!!.getString(R.string.channel_description)
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(CHANNEL_ID, name, importance)
      channel.description = description
      val notificationManager = contextRef.get()!!.getSystemService(NotificationManager::class.java)
      notificationManager.createNotificationChannel(channel)

    }
    Log.e("NOTIFIER", event.toString())
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
    with(contextRef.get()!!) {
      return when (event.eventType) {
        LinkingSiteEventType.PROCESSING -> throw IllegalStateException(
            "No notification for processing"
        )
        LinkingSiteEventType.SUCCESS -> getString(R.string.notification_title_success_link_account)
        LinkingSiteEventType.ACCOUNT_LOCKED -> getString(
            R.string.notification_title_error_link_account
        )
        LinkingSiteEventType.ALREADY_LOGGED_IN -> getString(
            R.string.notification_title_error_link_account
        )
        LinkingSiteEventType.INCORRECT_CREDENTIALS -> getString(
            R.string.notification_title_error_link_account
        )
        LinkingSiteEventType.TIMEOUT -> getString(R.string.notification_title_error_link_account)
        LinkingSiteEventType.TWO_FA -> getString(R.string.notification_title_progress_link_account)
        LinkingSiteEventType.TWO_FA_IMAGES -> getString(
            R.string.notification_title_progress_link_account
        )
        LinkingSiteEventType.SERVER_ERROR -> getString(
            R.string.notification_title_error_link_account
        )
        LinkingSiteEventType.CHECK_WEBSITE -> getString(
            R.string.notification_title_error_link_account
        )
      }.format(event.site.name)
    }
  }

  private fun description(): String {
    with(contextRef.get()!!) {
      return when (event.eventType) {
        PROCESSING -> throw IllegalStateException("No notification for processing")
        LinkingSiteEventType.SUCCESS -> getString(R.string.notification_message_linked).format(
            event.site.name
        )
        LinkingSiteEventType.ACCOUNT_LOCKED -> getString(
            R.string.notification_message_account_locked
        )
        LinkingSiteEventType.ALREADY_LOGGED_IN -> getString(R.string.notification_message_logged_in)
        LinkingSiteEventType.INCORRECT_CREDENTIALS -> getString(
            R.string.notification_message_incorrect_credentials
        )
        LinkingSiteEventType.TIMEOUT -> getString(R.string.notification_message_timeout)
        LinkingSiteEventType.TWO_FA -> getString(R.string.notification_message_more_info)
        LinkingSiteEventType.TWO_FA_IMAGES -> getString(R.string.notification_message_more_info)
        LinkingSiteEventType.SERVER_ERROR -> getString(R.string.notification_message_server_busy)
        LinkingSiteEventType.CHECK_WEBSITE -> getString(R.string.notification_message_check_website)
      }
    }
  }

  private fun pendingIntent(): PendingIntent {
    val builder = TaskStackBuilder.create(contextRef.get()!!)
    builder.addNextIntent(intent())
    return builder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)!!
  }

  private fun intent(): Intent {
    return when (event.eventType) {
      PROCESSING -> throw IllegalStateException("No notification for processing")
      TWO_FA -> LinkingSiteActivity.twoFaIntent(contextRef.get()!!, event)
      TWO_FA_IMAGES -> LinkingSiteActivity.twoFaIntent(contextRef.get()!!, event)
      SUCCESS -> SyncModule.homeIntentFactory.create(contextRef.get()!!)
      else -> LinkSiteActivity.newIntent(
          contextRef.get()!!, event.organization, event.site
      )
    }
  }

}

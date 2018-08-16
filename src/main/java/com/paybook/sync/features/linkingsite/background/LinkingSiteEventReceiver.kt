package com.paybook.sync.features.linkingsite.background

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.paybook.sync.SyncModule

/**
 * Created by Gerardo Teruel on 3/27/18.
 */


class LinkingSiteEventReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    val repository = SyncModule.linkingSiteRepository
    val event = LinkingSiteBroadcastService.parse(intent)
    repository.clear(event.jobId).subscribe()

    Log.e("RECEIVER", event.toString())
    if (resultCode == Activity.RESULT_CANCELED) {
      return
    }

    LinkingSiteEventNotifier(context, event).sendNotification()
  }

}

package com.paybook.sync.features.linkingsite.background

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Gerardo Teruel on 3/27/18.
 */

class LinkingSiteEventReceiver : BroadcastReceiver() {

  override fun onReceive(
    context: Context,
    intent: Intent
  ) {
    if (resultCode == Activity.RESULT_CANCELED) {
      return
    }

    val event = LinkingSiteBroadcastService.parse(intent)
    LinkingSiteEventNotifier(context, event).sendNotification()
  }

}

package com.paybook.sync.base

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by Gerardo Teruel on 1/21/18.
 */

/**
 * A base service designed to work with RxJava.
 */
abstract class BaseService : Service() {
  override fun onBind(intent: Intent): IBinder? {
    return null
  }

  abstract fun inject()

  override fun onCreate() {
    super.onCreate()
    inject()
  }

  override fun onStartCommand(
    intent: Intent,
    flags: Int,
    startId: Int
  ): Int {
    return super.onStartCommand(intent, flags, startId)
  }
}

package com.paybook.sync.features.linkingsite

import android.content.SharedPreferences
import android.util.Log
import com.paybook.core.schedulers.SchedulerProvider
import com.paybook.sync.SyncModule
import com.paybook.sync.util.FileStorage
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.LinkingSiteEventType
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by Gerardo Teruel on 3/27/18.
 */

class LinkingSiteRepository(
  private val sharedPreferences: SharedPreferences,
  private val fileStorage: FileStorage
) {

  companion object {
    private const val PREFIX = "EVT_"
  }

  private val schedulerProvider: SchedulerProvider = SyncModule.scheduler

  fun setBackgroundEvent(event: LinkingSiteEvent) {
    val type = event.eventType
    val value = type.ordinal

    fileStorage.store(key(event.jobId), event)
        .observeOn(schedulerProvider.io())
        .subscribe({
          Log.e("REPO", "Saved")
        }) {
          throw it
        }

    sharedPreferences.edit()
        .putInt(key(event.jobId), value)
        .apply()
  }

  fun event(jobId: String): Maybe<LinkingSiteEvent> {
    return fileStorage.retrieve(key(jobId))
  }

  fun clear(jobId: String): Observable<Boolean> {
    return Observable.defer {
      fileStorage.clear(key(jobId))
          .subscribe()
      sharedPreferences.edit()
          .remove(key(jobId))
          .apply()
      Observable.just(true)
    }
  }

  private fun key(jobId: String): String {
    return PREFIX + jobId
  }

}

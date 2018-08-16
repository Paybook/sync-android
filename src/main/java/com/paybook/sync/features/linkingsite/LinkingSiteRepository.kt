package com.paybook.sync.features.linkingsite

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
import com.paybook.core.schedulers.SchedulerProvider
import com.paybook.sync.SyncModule
import com.paybook.sync.util.FileStorage
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.LinkingSiteEventType
import io.reactivex.Completable
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

  @SuppressLint("ApplySharedPref")
  fun saveEvent(event: LinkingSiteEvent): Completable {
    return Completable.defer {
      val type = event.eventType
      val value = type.ordinal

      sharedPreferences.edit()
          .putInt(key(event.jobId), value)
          .commit()

      fileStorage.store(key(event.jobId), event)
          .observeOn(schedulerProvider.io())
    }
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

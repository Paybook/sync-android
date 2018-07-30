package com.paybook.sync.util

import android.content.Context
import io.reactivex.Observable
import io.reactivex.Single
import java.io.FileNotFoundException
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.lang.ref.WeakReference

/**
 * Created by Gerardo Teruel on 3/28/18.
 */

class FileStorage(context: Context) {

  private val contextReference = WeakReference<Context>(context)

  fun <T : Serializable> store(
    key: String,
    value: T
  ): Observable<Boolean> {
    return Observable.defer {
      val context = contextReference.get()!!.applicationContext
      try {
        context.openFileOutput(key, Context.MODE_PRIVATE)
            .use { os ->
              val objectOutputStream = ObjectOutputStream(os)
              objectOutputStream.writeObject(value)
              objectOutputStream.close()
            }
      } catch (e: IOException) {
        throw IllegalStateException("Couldn't open file for files", e)
      }

      Observable.just(true)
    }
  }

  fun <T> retrieve(key: String): Single<T>? {
    var ready = true
    val source = Single.defer {
      val context = contextReference.get()!!.applicationContext
      try {
        context.openFileInput(key)
            .use { `is` ->
              val objectInputStream = ObjectInputStream(`is`)
              @Suppress("UNCHECKED_CAST")
              Single.just<T>(objectInputStream.readObject() as T)
            }
      } catch (e: FileNotFoundException) {
        ready = false
        null
      } catch (e: IOException) {
        throw IllegalStateException(e)
      } catch (e: ClassNotFoundException) {
        throw IllegalStateException(e)
      }
    }
    if (ready) {
      return source
    }
    return null
  }

  fun clear(key: String): Observable<Boolean> {
    return Observable.defer {
      val context = contextReference.get()!!.applicationContext
      context.deleteFile(key)
      Observable.just(true)
    }
  }
}

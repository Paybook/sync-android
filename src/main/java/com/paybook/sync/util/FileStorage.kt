package com.paybook.sync.util

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
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
  ): Completable {
    return Completable.defer {
      val context = contextReference.get()!!.applicationContext
      try {
        context.openFileOutput(key, Context.MODE_PRIVATE)
            .use { os ->
              val objectOutputStream = ObjectOutputStream(os)
              objectOutputStream.writeObject(value)
              objectOutputStream.close()
            }
        Completable.complete()
      } catch (e: IOException) {
        Completable.error(IllegalStateException("Couldn't open file for files", e))
      }
    }
  }

  fun <T : Serializable> retrieve(key: String): Maybe<T> {
    return Maybe.defer {
      try {
        contextReference.get()!!.openFileInput(key)
            .use { inputStream ->
              val objectInputStream = ObjectInputStream(inputStream)
              @Suppress("UNCHECKED_CAST")
              Maybe.just(objectInputStream.readObject() as T)
            }
      } catch (e: FileNotFoundException) {
        Maybe.empty<T>()
      } catch (e: IOException) {
        throw IllegalStateException(e)
      } catch (e: ClassNotFoundException) {
        throw IllegalStateException(e)
      }

    }
  }

  fun clear(key: String): Observable<Boolean> {
    return Observable.defer {
      val context = contextReference.get()!!.applicationContext
      context.deleteFile(key)
      Observable.just(true)
    }
  }
}

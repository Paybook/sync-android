package com.paybook.sync.models

/**
 * Created by Gerardo Teruel on 5/30/18.
 */
import com.paybook.sync.unsucessful.SyncUnsuccesfulResponse
import com.paybook.sync.unsucessful.SyncUnsuccesfulResponseConverter
import com.paybook.sync.unsucessful.SyncUnsuccesfulResponseException
import okhttp3.ResponseBody
import retrofit2.adapter.rxjava2.Result
import java.io.IOException

/**
 * Created by Gerardo Teruel on 2/26/18.
 */

class SyncResult<T>(val result: Result<T>) {

  /**
   * @return true if a network error occurred.
   */
  val isNetworkError: Boolean
    get() = result.isError && result.error() is IOException

  /**
   * Returns true if an error occurred while attempting a network connection. If this is not a
   * [.isNetworkError] then, it should be treated as a programming/logic error.
   */
  //
  val isError: Boolean
    get() = result.isError

  val isSuccess: Boolean
    get() = !result.isError && result.response()!!.isSuccessful

  fun error(): Exception {
    return if (result.isError) {
      result.error() as Exception
    } else if (!result.response()!!.isSuccessful) {
      SyncUnsuccesfulResponseException(unsuccesfulResponse())
    } else {
      IllegalStateException("Trying to get an error from a successful result")
    }
  }

  /**
   * Return the http status code of the response.
   */
  fun code(): Int {
    if (isError) {
      throw IllegalStateException("Can't get code when the result was an error")
    }
    return result.response()!!.code()
  }

  /**
   * Returns the response body.
   *
   * @return the response body.
   * @throws IllegalStateException if there was a connection error OR the response wasn't succesful.
   */
  fun body(): T? {
    if (result.isError) {
      throw IllegalStateException("Can't get body from a result with an error")
    } else if (!result.response()!!.isSuccessful) {
      throw IllegalStateException("Can't get the body from an unsuccessful response")
    }
    return result.response()!!.body()
  }

  /**
   * @return the raw response.
   * @throws IllegalStateException if [.isError]
   */
  fun raw(): ResponseBody? {
    if (result.isError) {
      throw IllegalStateException("Can't get the body from an error result", error())
    }
    return if (!isSuccess) {
      result.response()!!.raw()
          .body()
    } else {
      result.response()!!.errorBody()
    }
  }

  private fun unsuccesfulResponse(): SyncUnsuccesfulResponse {
    return SyncUnsuccesfulResponseConverter().convert(result.response()!!.errorBody()!!)
  }
}

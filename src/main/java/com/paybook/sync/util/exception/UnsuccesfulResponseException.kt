package com.paybook.sync.util.exception

import retrofit2.Response

/**
 * Created by Gerardo Teruel on 6/1/18.
 */
class UnsuccesfulResponseException(val response: Response<*>) : RuntimeException(
    "An unsuccesful response occurred, error code: " + response.code()
) {

  fun string(): String {
    return response.errorBody()!!.string()
  }

}

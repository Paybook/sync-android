package com.paybook.sync.unsucessful

/**
 * Created by Gerardo Teruel on 7/23/18.
 */
data class SyncUnsuccesfulResponseException(
  val response: SyncUnsuccesfulResponse
) : RuntimeException(response.toString()) {

  fun describe() = response.message()
}


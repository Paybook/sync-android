package com.paybook.sync.unsucessful

/**
 * Created by Gerardo Teruel on 7/23/18.
 */
data class UnsuccesfulResponseException(
  val response: UnsuccesfulResponse
) : RuntimeException(response.toString())


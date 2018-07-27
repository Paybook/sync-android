package com.paybook.sync.unsucessful

import com.google.gson.annotations.SerializedName

/**
 * Created by Gerardo Teruel on 7/23/18.
 */
//@JsonClass(generateAdapter = true)
data class SyncUnsuccesfulResponse(
  @SerializedName("rid") val rid: String,
  @SerializedName("code") val code: Int,
  @SerializedName("message") val info: String?,
  @SerializedName("errors") val errors: List<String>?
) {

  fun message(): String {
    return info ?: errors?.toString() ?: rid
  }
}


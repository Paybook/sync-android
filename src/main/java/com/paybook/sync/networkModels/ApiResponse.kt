package com.paybook.sync.networkModels

import com.google.gson.annotations.SerializedName

/**
 * Created by Gerardo Teruel on 6/4/18.
 */
class ApiResponse<T> {
  @SerializedName("rid") var rid: String? = null
  @SerializedName("response") var response: T? = null
}

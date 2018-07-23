package com.paybook.sync.networkModels.twofa

import com.google.gson.annotations.SerializedName

/**
 * Created by Gerardo Teruel on 10/16/17.
 */

class TwoFaResponse {
  @SerializedName("rid") var rid: String? = null
  @SerializedName("status") var status: Boolean = false
}

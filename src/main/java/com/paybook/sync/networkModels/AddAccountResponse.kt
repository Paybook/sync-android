package com.paybook.sync.networkModels

import com.google.gson.annotations.SerializedName
import com.paybook.sync.entities.AddingAccount

/**
 * Created by Gerardo Teruel on 9/5/17.
 */

class AddAccountResponse {

  var rid: String? = null
  var response: Response? = null

  fun map(): AddingAccount {
    return AddingAccount(response!!.jobId!!, response!!.websocket!!)
  }

  class Response {
    @SerializedName("ws") internal var websocket: String? = null
    @SerializedName("id_job") internal var jobId: String? = null
  }
}

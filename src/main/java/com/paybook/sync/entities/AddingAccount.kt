package com.paybook.sync.entities

import java.io.Serializable

/**
 * Created by Gerardo Teruel on 10/17/17.
 */

class AddingAccount(
  val jobId: String,
  val websocket: String
) : Serializable

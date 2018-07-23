package com.paybook.sync.entities.twofa

import java.io.Serializable

/**
 * Created by Gerardo Teruel on 4/12/18.
 */
data class TwoFaImage(
  val uri: String,
  val value: Int
) : Serializable

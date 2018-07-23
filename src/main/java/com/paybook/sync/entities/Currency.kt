package com.paybook.sync.entities

import java.io.Serializable

/**
 * Created by Gerardo Teruel on 8/15/17.
 */

data class Currency(
  val id: String,
  val name: String,
  val code: String,
  val symbol: String = "$"
) : Serializable
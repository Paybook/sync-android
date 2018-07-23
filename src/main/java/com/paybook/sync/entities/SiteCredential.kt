package com.paybook.sync.entities

import java.io.Serializable

/**
 * Created by Gerardo Teruel on 9/1/17.
 */

data class SiteCredential(
  override val name: String,
  override val type: String,
  override val label: String
) : Serializable, Credential
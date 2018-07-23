package com.paybook.sync.entities.twofa

import com.paybook.sync.entities.Credential
import java.io.Serializable

/**
 * Created by Gerardo Teruel on 4/12/18.
 */
data class TwoFaCredential(
  override val name: String,
  override val type: String,
  override val label: String
) : Serializable, Credential

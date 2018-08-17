package com.paybook.sync.entities

import com.paybook.sync.entities.twofa.TwoFaImage
import com.paybook.sync.entities.twofa.TwoFaCredential
import java.io.Serializable

/**
 * Created by Gerardo Teruel on 4/12/18.
 */
data class LinkingSiteEvent(
  val organization: Organization,
  val site: Site,
  val jobId: String,
  val eventType: LinkingSiteEventType,
  val twoFaCredentials: List<TwoFaCredential>?,
  val twoFaImages: List<TwoFaImage>?,
  val label: String = ""
) : Serializable

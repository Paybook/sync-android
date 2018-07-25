package com.paybook.sync.features.linkingsite.twofa.simpletwofa

import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import com.paybook.sync.entities.twofa.TwoFaCredential
import java.io.Serializable

/**
 * Created by Gerardo Teruel on 3/26/18.
 */

data class TwoFaData(
  val organization: Organization,
  val site: Site,
  val credentials: List<TwoFaCredential>,
  val jobId: String
) : Serializable

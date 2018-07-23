package com.paybook.sync.features.linkingsite

import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import java.io.Serializable

/**
 * Created by Gerardo Teruel on 3/26/18.
 */

data class LinkingSiteData(
  val organization: Organization,
  val site: Site,
  val jobId: String
) : Serializable

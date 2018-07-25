package com.paybook.sync.features.linkingsite.twofa.twofaimages.model

import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import com.paybook.sync.entities.twofa.TwoFaImage
import java.io.Serializable

/**
 * Created by Gerardo Teruel on 11/13/17.
 */

data class TwoFaImagesData(
  val site: Site,
  val organization: Organization,
  val jobId: String,
  val twoFaImages: List<TwoFaImage>
) : Serializable

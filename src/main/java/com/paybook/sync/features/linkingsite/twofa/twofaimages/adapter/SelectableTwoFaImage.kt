package com.paybook.sync.features.linkingsite.twofa.twofaimages.adapter

import com.paybook.sync.entities.twofa.TwoFaImage

/**
 * Created by Gerardo Teruel on 11/13/17.
 */

data class SelectableTwoFaImage(
  var isSelected: Boolean,
  val twoFaImage: TwoFaImage
)
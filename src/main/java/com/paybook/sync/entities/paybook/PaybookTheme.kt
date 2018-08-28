package com.paybook.sync.entities.paybook

import android.support.annotation.ColorInt
import java.io.Serializable
import java.math.BigDecimal

/**
 * Created by Gerardo Teruel on 10/10/17.
 */

data class PaybookTheme(
  val id: String,
  val name: String,
  val icon: String,
  val balance: BigDecimal,
  @ColorInt val color: Int,
  val children: List<PaybookThemeType>
) : Serializable

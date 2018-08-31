package com.paybook.sync.entities.paybook

import android.support.annotation.ColorInt
import java.math.BigDecimal

/**
 * Created by Gerardo Teruel on 8/28/18.
 */
data class PaybookThemeType(
  val id: String,
  val name: String,
  val icon: String,
  val balance: BigDecimal,
  val children: List<Paybook>,
  @ColorInt val color: Int
)

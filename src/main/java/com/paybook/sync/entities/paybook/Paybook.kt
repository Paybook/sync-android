package com.paybook.sync.entities.paybook

import com.paybook.sync.entities.Currency
import java.io.Serializable
import java.math.BigDecimal

/**
 * Created by Gerardo Teruel on 5/16/17.
 */

public data class Paybook(
  val id: String,
  val paybookThemeId: String,
  val paybookThemeTypeId: String?,
  val title: String,
  val imageUrl: String?,
  val userImageUrl: String?,
  val balance: BigDecimal?,
  val icon: String,
  val currency: Currency,
  val children: List<Paybook>,
  val paybookTheme: PaybookTheme
) : Serializable {

  val isAccount: Boolean
    get() = paybookThemeId == "520d33d33b8e77a40c000000"

  val isBrand: Boolean
    get() = paybookThemeId == "523be6283b8e779c038b4578"

  val isManualAccount: Boolean
    get() = paybookThemeTypeId != null && paybookThemeTypeId == MANUAL_ACCOUNT_ID

  val isCategory: Boolean
    get() = paybookThemeId == "527ac4b7c690166a408b4567"

  companion object {
    const val MANUAL_ACCOUNT_ID = "520d34cb3b8e77cb0c000000"
    const val BRAND_ID = "523be6283b8e779c038b4578"
    const val CATEGORY_ID = "527ac4b7c690166a408b4567"
    const val ACCOUNT_ID = "520d33d33b8e77a40c000000"
  }
}

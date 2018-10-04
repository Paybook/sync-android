package com.paybook.sync.entities.paybook

import com.paybook.sync.entities.paybook.PaybookType.PAYBOOK
import com.paybook.sync.entities.paybook.PaybookType.THEME
import com.paybook.sync.entities.paybook.PaybookType.THEME_TYPE
import java.io.Serializable
import java.math.BigDecimal

/**
 * Created by Gerardo Teruel on 5/16/17.
 */

@Suppress("unused")
data class Paybook(
  val paybookId: String?,
  val paybookThemeId: String?,
  val paybookThemeTypeId: String?,
  val paybookParentId: String?,
  val currencyId: String,
  val ownerId: String?,
  val name: String,
  val avatar: String?,
  val balance: BigDecimal,
  val cover: String?,
  val color: Int,
  val icon: String,
  val children: List<Paybook>
) : Serializable {

  val id: String
    get() = paybookId ?: paybookThemeId ?: paybookThemeTypeId ?: throw IllegalStateException(
        "No id for paybook: $this"
    )

  val isManualAccount: Boolean
    get() = paybookThemeTypeId == MANUAL_ACCOUNT_ID

  val isCreditAccount: Boolean
    get() = paybookThemeTypeId == MANUAL_ACCOUNT_ID

  val isCategory: Boolean
    get() = paybookThemeId == CATEGORY_ID

  val isAccount: Boolean
    get() = paybookThemeId == ACCOUNT_ID

  val isBrand: Boolean
    get() = paybookThemeId == BRAND_ID

  fun isOwned(profileId: String) = ownerId == profileId

  val type: PaybookType by lazy {
    if (paybookThemeId != null && ((paybookId == null && paybookParentId == null) || paybookThemeId == paybookId)) {
      THEME
    } else if (paybookThemeTypeId != null && paybookId == null) {
      THEME_TYPE
    } else {
      PAYBOOK
    }
  }

  fun asPaybookTheme(): PaybookTheme {
    if (type != THEME) {
      throw IllegalStateException("Cannot create PaybookTheme from $type. Source: $this")
    }
    return PaybookTheme(
        id = paybookThemeId!!,
        name = name,
        icon = icon,
        balance = balance,
        children = children,
        color = color,
        cover = cover ?: ""
    )
  }

  fun asPaybookThemeType(): PaybookThemeType {
    if (type != THEME_TYPE) {
      throw IllegalStateException("Cannot create PaybookThemeType from $type. Source: $this")
    }
    return PaybookThemeType(
        id = paybookThemeTypeId!!,
        name = name,
        icon = icon,
        balance = balance,
        children = children,
        color = color,
        cover = cover ?: ""
    )
  }

  companion object {
    const val MANUAL_ACCOUNT_ID = "520d34cb3b8e77cb0c000000"
    const val CREDIT_ACCOUNT_ID = "520d3aa93b8e778e0d000002"
    const val BRAND_ID = "523be6283b8e779c038b4578"
    const val CATEGORY_ID = "527ac4b7c690166a408b4567"
    const val ACCOUNT_ID = "520d33d33b8e77a40c000000"
  }
}

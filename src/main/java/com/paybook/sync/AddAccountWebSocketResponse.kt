package com.paybook.sync

import com.google.gson.annotations.SerializedName
import com.paybook.sync.entities.LinkingSiteEventType
import com.paybook.sync.entities.LinkingSiteEventType.ACCOUNT_LOCKED
import com.paybook.sync.entities.LinkingSiteEventType.ALREADY_LOGGED_IN
import com.paybook.sync.entities.LinkingSiteEventType.CHECK_WEBSITE
import com.paybook.sync.entities.LinkingSiteEventType.INCORRECT_CREDENTIALS
import com.paybook.sync.entities.LinkingSiteEventType.PROCESSING
import com.paybook.sync.entities.LinkingSiteEventType.SERVER_ERROR
import com.paybook.sync.entities.LinkingSiteEventType.SUCCESS
import com.paybook.sync.entities.LinkingSiteEventType.TIMEOUT
import com.paybook.sync.entities.LinkingSiteEventType.TWO_FA
import com.paybook.sync.entities.LinkingSiteEventType.TWO_FA_IMAGES
import com.paybook.sync.entities.twofa.TwoFaCredential
import java.util.ArrayList

@Suppress("MemberVisibilityCanBePrivate")
/**
 * Created by Gerardo Teruel on 10/16/17.
 */

data class AddAccountWebSocketResponse(
  @SerializedName("code") var code: Int,
  @SerializedName("twofa") var twofa: List<TwoFa>? = null
) {

  val isProcessing: Boolean
    get() = code in 100..103

  val isSuccess: Boolean
    get() = code in 200..205

  val isError: Boolean
    get() = code == 301 || code in 401..403

  val isAccountLocked: Boolean
    get() = code == 405

  val checkWebsite: Boolean
    get() = code == 408

  val isUserLoggedIn: Boolean
    get() = code == 406

  val isTwoFaNeeded: Boolean
    get() = code == 410

  val isNormalTwoFaNeeded: Boolean
    get() = isTwoFaNeeded && !isImageTwoFaNeeded

  val isImageTwoFaNeeded: Boolean
    get() = isTwoFaNeeded && twofa!!.first().twoFaImages != null

  val isTimeOut: Boolean
    get() = code == 411 || code == 413

  val isServerError: Boolean
    get() = code in 500..599

  val credentials: List<TwoFaCredential>?
    get() = twofa?.map { TwoFaCredential(it.name, it.type, it.label) }

  val label: String
    get() = twofa?.firstOrNull()?.label ?: ""

  val imageOptions: List<TwoFaImageResponse>?
    get() = twofa?.firstOrNull()?.twoFaImages



  fun toEvent(): LinkingSiteEventType {
    return when {
      isSuccess -> SUCCESS
      isAccountLocked -> ACCOUNT_LOCKED
      isUserLoggedIn -> ALREADY_LOGGED_IN
      isError -> INCORRECT_CREDENTIALS
      isTimeOut -> TIMEOUT
      isImageTwoFaNeeded -> TWO_FA_IMAGES
      isNormalTwoFaNeeded -> TWO_FA
      isServerError -> SERVER_ERROR
      isProcessing -> PROCESSING
      checkWebsite -> CHECK_WEBSITE
      else -> throw IllegalStateException("Cannot linkingSite to event $this")
    }
  }

  fun isTerminal(): Boolean {
    val event = toEvent()
    return event in listOf(TIMEOUT, INCORRECT_CREDENTIALS, SERVER_ERROR, ACCOUNT_LOCKED, ALREADY_LOGGED_IN, SUCCESS)
  }
}

data class TwoFa(
  @SerializedName("name") var name: String,
  @SerializedName("type") var type: String,
  @SerializedName("label") var label: String,
  @SerializedName("options") var twoFaImages: List<TwoFaImageResponse>? = null
)

data class TwoFaImageResponse(
  @SerializedName("imgURL") var imgURL: String? = null,
  @SerializedName("imgBase64File") var imgBase64File: String? = null,
  @SerializedName("value") var value: Int
) {
  val isUrl: Boolean
    get() = imgURL != null
}
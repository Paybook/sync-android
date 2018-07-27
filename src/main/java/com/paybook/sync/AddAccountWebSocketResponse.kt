package com.paybook.sync

import com.google.gson.annotations.SerializedName
import com.paybook.sync.entities.LinkingSiteEventType
import com.paybook.sync.entities.LinkingSiteEventType.ACCOUNT_LOCKED
import com.paybook.sync.entities.LinkingSiteEventType.ALREADY_LOGGED_IN
import com.paybook.sync.entities.LinkingSiteEventType.INCORRECT_CREDENTIALS
import com.paybook.sync.entities.LinkingSiteEventType.PROCESSING
import com.paybook.sync.entities.LinkingSiteEventType.SERVER_ERROR
import com.paybook.sync.entities.LinkingSiteEventType.SUCCESS
import com.paybook.sync.entities.LinkingSiteEventType.TIMEOUT
import com.paybook.sync.entities.LinkingSiteEventType.TWO_FA
import com.paybook.sync.entities.LinkingSiteEventType.TWO_FA_IMAGES
import com.paybook.sync.entities.twofa.TwoFaCredential
import java.util.ArrayList

/**
 * Created by Gerardo Teruel on 10/16/17.
 */

class AddAccountWebSocketResponse(
  @SerializedName("code") var code: Int = 0,
  @SerializedName("twofa") var twofa: List<TwoFa>? = null
) {

  @Suppress("MemberVisibilityCanBePrivate")
  val isProcessing: Boolean
    get() = code in 100..105

  val isSuccess: Boolean
    get() = code in 200..203

  val isError: Boolean
    get() = code == 301 || code in 401..403

  val isAccountLocked: Boolean
    get() = code == 405 || code == 408

  val isUserLoggedIn: Boolean
    get() = code == 406

  val isNormalTwoFaNeeded: Boolean
    get() = code == 410 && twofa!![0].type != "twoFaImages"

  val isImageTwoFaNeeded: Boolean
    get() = code == 410 && twofa!![0].type == "twoFaImages"

  val isTimeOut: Boolean
    get() = code == 411 || code == 413

  val isServerError: Boolean
    get() = code in 500..599

  val credentials: List<TwoFaCredential>
    get() {
      val out = ArrayList<TwoFaCredential>()
      for (twofa in this.twofa!!) {
        out.add(TwoFaCredential(twofa.name!!, twofa.type!!, twofa.label!!))
      }
      return out
    }

  val imageOptions: List<TwoFaImageResponse>?
    get() = twofa!![0].twoFaImages

  val twoFaLabel: String?
    get() = twofa!![0].label

  val isTwoFaNeeded: Boolean
    get() = code == 410

  override fun toString(): String {
    return "AddAccountWebSocketResponse{" + "code=" + code + ", twofa=" + twofa + '}'.toString()
  }

  fun toEvent(): LinkingSiteEventType {
    return when {
      isSuccess -> SUCCESS
      isAccountLocked -> ACCOUNT_LOCKED
      isUserLoggedIn -> ALREADY_LOGGED_IN
      isError -> INCORRECT_CREDENTIALS
      isTimeOut -> TIMEOUT
      isNormalTwoFaNeeded -> TWO_FA
      isImageTwoFaNeeded -> TWO_FA_IMAGES
      isServerError -> SERVER_ERROR
      isProcessing -> PROCESSING
      else -> throw IllegalStateException("Cannot linkingSite to event " + toString())
    }
  }

  fun isTerminal(): Boolean {
    val event = toEvent()
    return event in listOf(TIMEOUT, INCORRECT_CREDENTIALS, SERVER_ERROR, ACCOUNT_LOCKED, ALREADY_LOGGED_IN, SUCCESS)
  }

}

data class TwoFa(
  @SerializedName("name") var name: String? = null,
  @SerializedName("type") var type: String? = null,
  @SerializedName("label") var label: String? = null,
  @SerializedName("twoFaImages") var twoFaImages: List<TwoFaImageResponse>? = null
)

data class TwoFaImageResponse(
  @SerializedName("imgURL") var imgUrl: String? = null,
  @SerializedName("imgBase64File") var img: String? = null,
  @SerializedName("value") var value: Int = 0
) {
  val isUrl: Boolean
    get() = imgUrl != null
}
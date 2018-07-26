package com.paybook.sync

import com.google.gson.annotations.SerializedName
import com.paybook.sync.entities.LinkingSiteEventType
import com.paybook.sync.entities.twofa.TwoFaCredential
import java.util.ArrayList

/**
 * Created by Gerardo Teruel on 10/16/17.
 */

class AddAccountWebSocketResponse {

  @SerializedName("code") var code: Int = 0
  @SerializedName("twofa") var twofa: List<TwoFa>? = null

  val isProcessing: Boolean
    get() = code in 100..105

  val isSuccess: Boolean
    get() = code in 200..203

  val isError: Boolean
    get() = code == 301 || code in 401..403

  val isAccountLocked: Boolean
    get() = code == 405

  val isUserLoggedIn: Boolean
    get() = code == 406

  val isNormalTwoFaNeeded: Boolean
    get() = code == 410 && twofa!![0].type != "twoFaImages"

  val isImageTwoFaNeeded: Boolean
    get() = code == 410 && twofa!![0].type == "twoFaImages"

  val isTimeOut: Boolean
    get() = code == 411

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
    return if (isSuccess) {
      LinkingSiteEventType.SUCCESS
    } else if (isAccountLocked) {
      LinkingSiteEventType.ACCOUNT_LOCKED
    } else if (isUserLoggedIn) {
      LinkingSiteEventType.ALREADY_LOGGED_IN
    } else if (isError) {
      LinkingSiteEventType.INCORRECT_CREDENTIALS
    } else if (isTimeOut) {
      LinkingSiteEventType.TIMEOUT
    } else if (isNormalTwoFaNeeded) {
      LinkingSiteEventType.TWO_FA
    } else if (isImageTwoFaNeeded) {
      LinkingSiteEventType.TWO_FA_IMAGES
    } else if (isServerError) {
      LinkingSiteEventType.SERVER_ERROR
    } else if (isProcessing) {
      LinkingSiteEventType.PROCESSING
    } else {
      throw IllegalStateException("Cannot linkingSite to event " + toString())
    }
  }

  class TwoFa {
    @SerializedName("name") internal var name: String? = null
    @SerializedName("type") internal var type: String? = null
    @SerializedName("label") internal var label: String? = null
    @SerializedName("twoFaImages") internal var twoFaImages: List<TwoFaImageResponse>? = null

    override fun toString(): String {
      return ("TwoFa{"
          + "name='"
          + name
          + '\''.toString()
          + ", type='"
          + type
          + '\''.toString()
          + ", label='"
          + label
          + '\''.toString()
          + '}'.toString())
    }
  }

  class TwoFaImageResponse {
    @SerializedName("imgURL") var imgUrl: String? = null
      internal set
    @SerializedName("imgBase64File") var img: String? = null
      internal set
    @SerializedName("value") var value: Int = 0
      internal set

    val isUrl: Boolean
      get() = imgUrl != null

    override fun toString(): String {
      return ("TwoFaImageResponse{"
          + "imgUrl='"
          + imgUrl
          + '\''.toString()
          + ", img='"
          + img
          + '\''.toString()
          + ", value="
          + value
          + '}'.toString())
    }
  }
}

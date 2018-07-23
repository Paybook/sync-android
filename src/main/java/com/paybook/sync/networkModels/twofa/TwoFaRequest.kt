package com.paybook.sync.networkModels.twofa

import com.google.gson.annotations.SerializedName

/**
 * Created by Gerardo Teruel on 10/16/17.
 */

class TwoFaRequest(@field:SerializedName("twofa") internal var twofa: Map<String, Any>)

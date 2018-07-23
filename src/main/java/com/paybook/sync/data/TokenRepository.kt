package com.paybook.sync.data

import android.annotation.SuppressLint
import android.content.SharedPreferences

/**
 * Created by Gerardo Teruel on 7/18/18.
 */
class TokenRepository(
  private val sharedPreferences: SharedPreferences
) {

  companion object {
    private const val KEY = "com.paybook.sync.data.token"
  }

  @SuppressLint("ApplySharedPref")
  // Since we use rxjava we don't need the system to handle threading. Also using apply has led to
  // race conditions when retrieving the profile immediately after the login.
  fun setToken(token: String) {
    sharedPreferences.edit()
        .putString(KEY, token)
        .commit()
  }

  fun token(): String {
    val token = rawToken()
    if (token.isEmpty()) {
      throw IllegalStateException("Cannot get token that doesnt exist")
    }
    return token
  }

  fun rawToken(): String = sharedPreferences.getString(KEY, "")

  @SuppressLint("ApplySharedPref")
  // See setToken.
  fun clear() {
    sharedPreferences.edit()
        .remove(KEY)
        .commit()
  }
}
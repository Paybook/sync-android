package com.paybook.sync.entities

/**
 * Created by Gerardo Teruel on 3/26/18.
 */

enum class LinkingSiteEventType {
  PROCESSING,
  SUCCESS,
  ACCOUNT_LOCKED,
  ALREADY_LOGGED_IN,
  INCORRECT_CREDENTIALS,
  TIMEOUT,
  TWO_FA,
  TWO_FA_IMAGES,
  SERVER_ERROR
}

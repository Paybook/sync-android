package com.paybook.sync.entities.credential

/**
 * Created by Gerardo Teruel on 7/12/18.
 */
data class CredentialWithAccounts(
  val credential: Credential,
  val accounts: List<CredentialAccount>
)

package com.paybook.sync.entities

import java.io.Serializable

/**
 * Created by Gerardo Teruel on 9/1/17.
 */

/**
 * Represents an organization with different [Site]s representing the accounts that the
 * organization provides. Should be instantiated using the [.Builder] method. The
 * natural order of organizations is to be sorted by their name.
 */
data class Organization(
  val id: String,
  val typeId: String,
  val name: String,
  val avatar: String,
  val coverImageUrl: String,
  val sites: List<Site>,
  val countryId: String?
) : Comparable<Organization>, Serializable {

  val isBank: Boolean
    get() = this.typeId == "56cf4f5b784806cf028b4568"

  val isGovernment: Boolean
    get() = this.typeId == "56cf4f5b784806cf028b4569"

  val isUtility: Boolean
    get() = this.typeId == "57fed63a78480609038b4567"

  val isDigitalWallet: Boolean
    get() = this.typeId == "57fed63a78480609038b4568"

  /** Two organizations are equal if they share the same paybookId.  */
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || javaClass != other.javaClass) return false

    val that = other as Organization?

    return id == that!!.id
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  override fun compareTo(other: Organization): Int {
    return this.name.compareTo(other.name)
  }

}

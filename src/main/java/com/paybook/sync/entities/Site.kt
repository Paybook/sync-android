package com.paybook.sync.entities

import java.io.Serializable

/**
 * Created by Gerardo Teruel on 9/1/17.
 */

data class Site(
  val id: String,
  val name: String,
  val credentials: List<SiteCredential>,
  val isPersonal: Boolean = true
) : Comparable<Site>, Serializable {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || javaClass != other.javaClass) return false

    val site = other as Site?

    return id == site!!.id
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  override fun compareTo(other: Site): Int {
    return this.name.compareTo(other.name)
  }
}

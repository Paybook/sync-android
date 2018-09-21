package com.paybook.sync.entities

import java.io.Serializable

/**
 * Created by Gerardo Teruel on 9/1/17.
 */

class Country(
  val id: String,
  val name: String,
  val code: String
) : Comparable<Country>, Serializable {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || javaClass != other.javaClass) return false

    val country = other as Country?

    return id == country!!.id
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  override fun compareTo(other: Country): Int {
    return this.name.compareTo(other.name)
  }

  override fun toString(): String {
    return ("Country{"
        + "paybookId='"
        + id
        + '\''.toString()
        + ", name='"
        + name
        + '\''.toString()
        + ", code='"
        + code
        + '\''.toString()
        + '}'.toString())
  }

  companion object {
    val default: Country
      get() = Country("51ad44b83b8e7763120003c4", "Mexico", "MXN")
  }
}

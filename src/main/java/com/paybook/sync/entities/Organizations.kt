package com.paybook.sync.entities

import java.io.Serializable

/**
 * Created by Gerardo Teruel on 10/23/17.
 */

class Organizations(
  val organizations: List<Organization> = emptyList()
) : Serializable

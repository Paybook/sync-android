package com.paybook.sync.entities

/**
 * Created by Gerardo Teruel on 9/5/17.
 */

class LinkSiteRequest(
  val organization: Organization,
  val site: Site,
  val credentials: Map<String, String>
)

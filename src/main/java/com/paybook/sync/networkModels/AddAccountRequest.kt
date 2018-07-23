package com.paybook.sync.networkModels

import com.google.gson.annotations.SerializedName
import com.paybook.sync.entities.LinkSiteRequest

/**
 * Created by Gerardo Teruel on 9/5/17.
 */

class AddAccountRequest(linkSiteRequest: LinkSiteRequest) {
  @SerializedName("id_site") var siteId: String = linkSiteRequest.site.id
  @SerializedName("credentials") var credentials: Map<String, String> = linkSiteRequest.credentials
}

package com.paybook.sync.networkModels

import com.google.gson.annotations.SerializedName
import com.paybook.sync.SyncModule
import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import com.paybook.sync.entities.SiteCredential
import java.util.ArrayList

/**
 * Created by Gerardo Teruel on 9/1/17.
 */

class OrganizationsResponse {

  var rid: String? = null
  var response: List<Response>? = null

  fun map(): List<Organization> {
    return response!!.map {
      Organization(
          id = it.id!!,
          name = it.name!!,
          avatar = SyncModule.imagesUrlBase + it.avatar!!,
          coverImageUrl = SyncModule.imagesUrlBase + it.cover!!,
          typeId = it.typeId!!,
          countryId = it.countryId,
          sites = it.map()
      )
    }
  }

  class Response {
    @SerializedName("id_site_organization") var id: String? = null
    @SerializedName("id_site_organization_type") var typeId: String? = null
    @SerializedName("id_country") var countryId: String? = null
    @SerializedName("name") var name: String? = null
    @SerializedName("avatar") var avatar: String? = null
    @SerializedName("small_cover") var cover: String? = null
    @SerializedName("sites") var sites: List<SiteResponse>? = null

    fun map(): List<Site> {
      return sites!!.map {
        it.map()
      }
    }
  }

  class SiteResponse {
    @SerializedName("id_site") var id: String? = null
    @SerializedName("name") var name: String? = null
    @SerializedName("credentials") var credentials: List<Credential>? = null

    fun map(): Site {
      val credentials = ArrayList<SiteCredential>()
      for (c in this.credentials!!) {
        credentials.add(c.map())
      }

      return Site(id!!, name!!, credentials)
    }
  }

  class Credential {
    @SerializedName("name") var name: String? = null
    @SerializedName("type") var type: String? = null
    @SerializedName("label") var label: String? = null

    fun map(): SiteCredential {
      return SiteCredential(name!!, type!!, label!!)
    }
  }

}

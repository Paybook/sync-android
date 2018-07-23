package com.paybook.sync.features.linksite

import com.paybook.sync.features.linkingsite.LinkingSiteData
import com.paybook.sync.entities.AddingAccount
import com.paybook.sync.entities.LinkSiteRequest
import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import io.reactivex.disposables.Disposable

/**
 * Created by Gerardo Teruel on 9/5/17.
 */

interface LinkSiteContract {

  interface View {
    fun showOrganization(organization: Organization)
    fun showSite(site: Site)
    fun startLinkingAccount(addingAccount: AddingAccount)
    fun showNetworkError()
    fun showLoadingIndicator()
    fun hideLoadingIndicator()
    fun showAccountConnected()
  }

  interface Presenter {
    fun subscribe()
    fun link(linkSiteRequest: LinkSiteRequest): Disposable
  }

  interface Navigator {
    fun openLinkingSiteScreen(linkingSiteData: LinkingSiteData)
  }

}

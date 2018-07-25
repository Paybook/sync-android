package com.paybook.sync.features.linkingsite.twofa.simpletwofa

import com.paybook.sync.features.linkingsite.LinkingSiteData
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import com.paybook.sync.entities.twofa.TwoFaCredential
import io.reactivex.disposables.Disposable

/**
 * Created by Gerardo Teruel on 10/16/17.
 */

interface TwoFaContract {

  interface View {
    fun showOrganization(organization: Organization)
    fun showSite(site: Site)
    fun showCredentials(siteCredentials: List<TwoFaCredential>)
    fun showNetworkError()
    fun showLoadingIndicator()
    fun hideLoadingIndicator()
  }

  interface Presenter {
    fun onSendCredentials(event: LinkingSiteEvent, credentials: Map<String, String>): Disposable?
  }

  interface Navigator {
    fun openLinkingSiteScreen(data: LinkingSiteData)
  }
}

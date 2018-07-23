package com.paybook.sync.features.linksite

import com.paybook.core.exception.OnErrorNotImplementedException
import com.paybook.sync.SyncModule
import com.paybook.sync.networkModels.AddAccountRequest
import com.paybook.sync.features.linkingsite.LinkingSiteData
import com.paybook.sync.useCases.LinkAccountUseCase
import com.paybook.sync.entities.LinkSiteRequest
import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import io.reactivex.disposables.Disposable

/**
 * Created by Gerardo Teruel on 9/5/17.
 */

class LinkSitePresenter(
  private val view: LinkSiteContract.View,
  private val navigator: LinkSiteContract.Navigator,
  private val useCase: LinkAccountUseCase,
  private val organization: Organization,
  private val site: Site
) : LinkSiteContract.Presenter {

  private val schedulerProvider = SyncModule.scheduler
  override fun subscribe() {
    view.showOrganization(organization)
    view.showSite(site)
  }

  override fun link(linkSiteRequest: LinkSiteRequest): Disposable {
    view.showLoadingIndicator()
    return useCase.addAccount(AddAccountRequest(linkSiteRequest))
        .observeOn(schedulerProvider.ui())
        .subscribe({ result ->
          view.hideLoadingIndicator()
          when {
            result.isNetworkError -> view.showNetworkError()
            result.isError -> {
            }
            else -> {
              val addingAccount = result.body()!!.map()
              val data = LinkingSiteData(organization, site, addingAccount.jobId)
              navigator.openLinkingSiteScreen(data)
              view.startLinkingAccount(addingAccount)
            }
          }
        }, { t ->
          OnErrorNotImplementedException.rethrow(t, javaClass) }
        )
  }
}

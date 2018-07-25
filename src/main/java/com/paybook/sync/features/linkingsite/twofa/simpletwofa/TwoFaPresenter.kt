package com.paybook.sync.features.linkingsite.twofa.simpletwofa

import com.paybook.core.exception.OnErrorNotImplementedException
import com.paybook.sync.SyncModule
import com.paybook.sync.features.linkingsite.LinkingSiteData
import com.paybook.sync.useCases.VerifyTwoFaUseCase
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.unsucessful.UnsuccesfulResponseException
import io.reactivex.disposables.Disposable
import java.io.IOException

/**
 * Created by Gerardo Teruel on 10/16/17.
 */

class TwoFaPresenter(
  private val view: TwoFaContract.View,
  private val navigator: TwoFaContract.Navigator,
  private val verifyTwoFaUseCase: VerifyTwoFaUseCase
) : TwoFaContract.Presenter {
  private val schedulerProvider = SyncModule.scheduler

  override fun onSendCredentials(event: LinkingSiteEvent, credentials: Map<String, String>): Disposable? {
    view.showLoading()
    return verifyTwoFaUseCase.verify(event.jobId, credentials)
        .observeOn(schedulerProvider.ui())
        .subscribe({ it ->
          view.hideLoading()
          when {
            it.isNetworkError -> view.showNetworkError()
            it.isError -> {
              val error = it.error()
              when (error) {
                is IOException -> view.showNetworkError()
                is UnsuccesfulResponseException -> view.showUnexpectedError(error.response.message())
                else -> throw error
              }
            }
            else -> {
              val linkingSiteData = LinkingSiteData(event.organization, event.site, event.jobId)
              navigator.openLinkingSiteScreen(linkingSiteData)
            }
          }
        }) { t ->
          OnErrorNotImplementedException.rethrow(t, javaClass)
        }
  }

}

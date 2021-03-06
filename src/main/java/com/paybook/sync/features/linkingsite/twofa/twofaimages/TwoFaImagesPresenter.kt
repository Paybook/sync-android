package com.paybook.sync.features.linkingsite.twofa.twofaimages

import com.paybook.core.exception.OnErrorNotImplementedException
import com.paybook.core.schedulers.SchedulerProvider
import com.paybook.sync.SyncModule
import com.paybook.sync.useCases.VerifyTwoFaImagesUseCase
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.twofa.TwoFaImage
import io.reactivex.disposables.Disposable

/**
 * Created by Gerardo Teruel on 11/13/17.
 */

class TwoFaImagesPresenter(
  private val view: TwoFaImagesContract.View,
  private val navigator: TwoFaImagesContract.Navigator,
  private val twoFaImagesUseCase: VerifyTwoFaImagesUseCase,
  private val schedulerProvider: SchedulerProvider = SyncModule.scheduler
) : TwoFaImagesContract.Presenter {

  override fun submitImage(
    event: LinkingSiteEvent,
    value: TwoFaImage?
  ): Disposable? {
    if (value == null) {
      view.showPleaseSelectImageMessage()
      return null
    }
    view.showLoading()
    return twoFaImagesUseCase.verify(event.jobId, value.value)
        .observeOn(schedulerProvider.ui())
        .subscribe({ response ->
          view.hideLoading()
          when {
            response.isNetworkError -> view.showNetworkError()
            response.isError -> view.showUnexpectedError()
            response.isSuccess -> navigator.openLinkingSite(event)
          }
        }) { t ->
          throw OnErrorNotImplementedException(t)
        }
  }

  override fun subscribe(event: LinkingSiteEvent) {
    view.show(event)
  }

  override fun onFileCantBeLoaded() {
    view.showUnexpectedError()
  }

  override fun onImageSelected(file: TwoFaImage) {
    view.showImageSelected(file)
  }

  override fun onClose() {

  }
}

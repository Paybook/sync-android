package com.paybook.sync.features.linkingsite.twofa.twofaimages

import com.paybook.core.exception.OnErrorNotImplementedException
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
  private val twoFaImagesUseCase: VerifyTwoFaImagesUseCase
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
        .subscribe({ response ->
          view.hideLoading()
          when {
            response.isNetworkError -> view.showNetworkError()
            response.isError -> view.showUnexpectedError()
            response.isSuccess -> navigator.openLinkingSite(event)
          }
        }) { t -> OnErrorNotImplementedException.rethrow(t, javaClass) }
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

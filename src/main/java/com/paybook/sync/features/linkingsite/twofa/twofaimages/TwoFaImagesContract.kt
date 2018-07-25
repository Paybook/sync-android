package com.paybook.sync.features.linkingsite.twofa.twofaimages

import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.twofa.TwoFaImage
import io.reactivex.disposables.Disposable

/**
 * Created by Gerardo Teruel on 11/13/17.
 */

interface TwoFaImagesContract {

  interface View {
    fun show(event: LinkingSiteEvent)
    fun showImageSelected(twoFaImage: TwoFaImage)
    fun showNetworkError()
    fun showUnexpectedError()
    fun showLoading()
    fun hideLoading()
    fun showPleaseSelectImageMessage()
  }

  interface Presenter {
    fun submitImage(event: LinkingSiteEvent, value: TwoFaImage?): Disposable?
    fun subscribe(event: LinkingSiteEvent)
    fun onFileCantBeLoaded()
    fun onImageSelected(file: TwoFaImage)
    fun onClose()
  }

  interface Navigator {
    fun openLinkingSite(event: LinkingSiteEvent)
  }
}

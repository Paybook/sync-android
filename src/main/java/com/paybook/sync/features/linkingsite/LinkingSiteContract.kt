package com.paybook.sync.features.linkingsite

import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.LinkingSiteEventType
import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import io.reactivex.disposables.Disposable

/**
 * Created by Gerardo Teruel on 3/26/18.
 */

interface LinkingSiteContract {

  interface View {
    fun registerForLinkingSiteEvents()
    fun hideNotification(jobId: String)
  }

  interface Presenter {
    fun onEvent(event: LinkingSiteEvent)
    fun onTwoFa(event: LinkingSiteEvent)
    fun onTwoFaImages(event: LinkingSiteEvent)

    fun onReattemptLink(site: Site, organization: Organization)

    fun onGoToHome()

    fun subscribe(jobId: String): Disposable?
  }

  interface Navigator {
    fun openLoading()
    fun openSuccess()
    fun openError(reason: String, organization: Organization, site:Site)
    fun openHome()
    fun openLinkInstitution(organization: Organization, site: Site)
    fun openTwoFaScreen(event: LinkingSiteEvent)
    fun openTwoFaImagesScreen(event: LinkingSiteEvent)
  }

  interface Messages {
    fun titleError(): String
    fun descriptionAccountLocked(): String
    fun descriptionAlreadyLoggedIn(): String
    fun descriptionIncorrectCredentials(): String
    fun descriptionTimeOut(): String
  }
}

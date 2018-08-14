package com.paybook.sync.features.linkingsite

import com.paybook.core.exception.OnErrorNotImplementedException
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.LinkingSiteEventType.ACCOUNT_LOCKED
import com.paybook.sync.entities.LinkingSiteEventType.ALREADY_LOGGED_IN
import com.paybook.sync.entities.LinkingSiteEventType.CHECK_WEBSITE
import com.paybook.sync.entities.LinkingSiteEventType.INCORRECT_CREDENTIALS
import com.paybook.sync.entities.LinkingSiteEventType.PROCESSING
import com.paybook.sync.entities.LinkingSiteEventType.SERVER_ERROR
import com.paybook.sync.entities.LinkingSiteEventType.SUCCESS
import com.paybook.sync.entities.LinkingSiteEventType.TIMEOUT
import com.paybook.sync.entities.LinkingSiteEventType.TWO_FA
import com.paybook.sync.entities.LinkingSiteEventType.TWO_FA_IMAGES
import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import io.reactivex.disposables.Disposable

/**
 * Created by Gerardo Teruel on 3/26/18.
 */

class LinkingSitePresenter(
  private val view: LinkingSiteContract.View,
  private val navigator: LinkingSiteContract.Navigator,
  private val messages: LinkingSiteContract.Messages,
  private val repository: LinkingSiteRepository
) : LinkingSiteContract.Presenter {

  override fun onEvent(event: LinkingSiteEvent) {
    with(messages) {
      when (event.eventType) {
        SUCCESS -> navigator.openSuccess()
        INCORRECT_CREDENTIALS -> navigator.openError(
            descriptionIncorrectCredentials(), event.organization, event.site
        )
        ACCOUNT_LOCKED -> navigator.openError(
            descriptionAccountLocked(), event.organization, event.site
        )
        ALREADY_LOGGED_IN -> navigator.openError(
            descriptionAlreadyLoggedIn(), event.organization, event.site
        )
        TIMEOUT -> navigator.openError(descriptionAlreadyLoggedIn(), event.organization, event.site)
        SERVER_ERROR -> navigator.openError("", event.organization, event.site)
        PROCESSING -> navigator.openLoading()
        CHECK_WEBSITE -> navigator.openError(
            descriptionVisitWebsite(), event.organization, event.site
        )
        TWO_FA -> navigator.openTwoFaScreen(event)
        TWO_FA_IMAGES -> navigator.openTwoFaImagesScreen(event)
      }
    }
  }

  override fun subscribe(jobId: String): Disposable? {
    view.registerForLinkingSiteEvents()
    return repository.event(jobId)
        .subscribe({
          onEvent(it)
        }, {
          throw OnErrorNotImplementedException(it)
        }) {
          navigator.openLoading()
        }
  }

  override fun onReattemptLink(
    site: Site,
    organization: Organization
  ) {
    navigator.openLinkInstitution(organization, site)
  }

  override fun onGoToHome() {
    navigator.openHome()
  }
}

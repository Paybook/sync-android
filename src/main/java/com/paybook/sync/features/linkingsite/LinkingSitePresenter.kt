package com.paybook.sync.features.linkingsite

import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.LinkingSiteEventType.ACCOUNT_LOCKED
import com.paybook.sync.entities.LinkingSiteEventType.ALREADY_LOGGED_IN
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
    if (event.eventType == TWO_FA || event.eventType == TWO_FA_IMAGES) {
      throw IllegalArgumentException("$event must be handled in its appropriate method.")
    }
    with(messages) {
      when (event.eventType) {
        SUCCESS -> navigator.openSuccess()
        INCORRECT_CREDENTIALS ->
          navigator.openError(descriptionIncorrectCredentials(), event.organization, event.site)
        ACCOUNT_LOCKED ->
          navigator.openError(descriptionAccountLocked(), event.organization, event.site)
        ALREADY_LOGGED_IN ->
          navigator.openError(descriptionAlreadyLoggedIn(), event.organization, event.site)
        TIMEOUT ->
          navigator.openError(descriptionAlreadyLoggedIn(), event.organization, event.site)
        SERVER_ERROR ->
          navigator.openError("", event.organization, event.site)
        PROCESSING -> Unit
        else -> throw IllegalStateException("Unexpected linking site event found $event")
      }
    }
  }

  override fun subscribe(jobId: String): Disposable? {
    view.registerForLinkingSiteEvents()
    navigator.openLoading()
    return null
//    val event = repository.lastBackgroundEventType(jobId)
//    if (event == null) {
//      view.registerForLinkingSiteEvents()
//      return null
//    }
//
//    return when (event) {
//      TWO_FA -> {
//        repository.event(jobId)
//            .observeOn(schedulerProvider.ui())
//            .doOnNext { d -> onTwoFa(d) }
//      }
//      TWO_FA_IMAGES -> {
//        repository.event(jobId)
//            .observeOn(schedulerProvider.ui())
//            .doOnNext { d -> onTwoFaImages(d) }
//      }
//      else -> {
//        Observable.just(true)
//            .observeOn(schedulerProvider.ui())
//            .doOnNext { onEvent(event) }
//      }
//    }.doOnNext { view.hideNotification(jobId) }
//        .observeOn(schedulerProvider.io())
//        .flatMap {
//          repository.clear(jobId)
//        }
//        .subscribe()
  }

  override fun onTwoFa(event: LinkingSiteEvent) {
    navigator.openTwoFaScreen(event)
  }

  override fun onTwoFaImages(event: LinkingSiteEvent) {
    navigator.openTwoFaImagesScreen(event)
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

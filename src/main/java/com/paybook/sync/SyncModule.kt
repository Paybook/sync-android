package com.paybook.sync

import android.content.Context
import android.support.annotation.IdRes
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeModule
import com.paybook.core.imageLoader.ImageUrlLoaderFactory
import com.paybook.core.schedulers.AndroidScheduler
import com.paybook.core.schedulers.SchedulerProvider
import com.paybook.core.util.IntentFactory
import com.paybook.sync.data.OrganizationsRepository
import com.paybook.sync.data.SitesRepository
import com.paybook.sync.data.TokenRepository
import com.paybook.sync.features.linkingsite.LinkingSiteRepository
import com.paybook.sync.util.FileStorage
import com.paybook.sync.util.PicassoImageUrlLoaderFactory
import okhttp3.Interceptor

/**
 * Created by Gerardo Teruel on 5/28/18.
 */
class SyncModule private constructor() {

  companion object {

    var scheduler: SchedulerProvider = AndroidScheduler()

    lateinit var imageUrlLoaderFactory: ImageUrlLoaderFactory

    lateinit var syncService: SyncService
    lateinit var sitesRepository: SitesRepository
    lateinit var organizationsRepository: OrganizationsRepository
    lateinit var linkingSiteRepository: LinkingSiteRepository
    lateinit var tokenRepository: TokenRepository

    lateinit var homeIntentFactory: IntentFactory
    lateinit var imagesUrlBase: String
    lateinit var permission: String
    @IdRes var notificationIcon: Int = 0

    var isTest: Boolean = false

    fun configure(
      homeIntentFactory: IntentFactory,
      token: String? = null,
      icon: Int = R.drawable.sync_icon
    ) {
      this.homeIntentFactory = homeIntentFactory
      this.notificationIcon = icon
      token?.let {
        this.tokenRepository.setToken(it)
      }
    }

    fun init(
      context: Context,
      url: String,
      imagesUrlBase: String,
      permission: String,
      isTest: Boolean = false,
      interceptor: Interceptor? = null
    ) {

      val sharedPreferences = context.getSharedPreferences("sync", Context.MODE_PRIVATE)
      val fileStorage = FileStorage(context)
      this.tokenRepository = TokenRepository(sharedPreferences)
      this.linkingSiteRepository = LinkingSiteRepository(sharedPreferences, fileStorage)

      val syncClient = SyncClient(url, tokenRepository, interceptor)
      this.syncService = syncClient.service
      this.imagesUrlBase = imagesUrlBase

      this.sitesRepository = SitesRepository()
      this.organizationsRepository = OrganizationsRepository(syncService, sitesRepository)

      this.imageUrlLoaderFactory = PicassoImageUrlLoaderFactory(imagesUrlBase)

      this.imagesUrlBase = imagesUrlBase
      this.permission = permission
      this.isTest = isTest

      Iconify.with(FontAwesomeModule())
    }
  }

}

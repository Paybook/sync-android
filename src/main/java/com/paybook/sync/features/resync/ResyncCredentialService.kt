package com.paybook.sync.features.resync

import android.app.Service
import android.content.Context
import android.content.Intent
import com.paybook.sync.SyncModule
import com.paybook.sync.base.BaseService
import com.paybook.sync.data.OrganizationsRepository
import com.paybook.sync.data.SitesRepository
import com.paybook.sync.entities.AddingAccount
import com.paybook.sync.features.linkingsite.background.LinkingSiteBroadcastService
import io.reactivex.disposables.Disposable

/**
 * Created by Gerardo Teruel on 2/5/18.
 */

class ResyncCredentialService : BaseService() {

  companion object {
    const val IK_SITE = "com.paybook.sync.resync.site"
    const val IK_ORGANIZATION = "com.paybook.sync.resync.organization"
    const val IK_ACCOUNT = "com.paybook.sync.resync.account"

    fun newIntent(
      from: Context,
      siteId: String,
      organizationId: String,
      addingAccount: AddingAccount
    ): Intent {
      val i = Intent(from, ResyncCredentialService::class.java)
      i.putExtra(IK_SITE, siteId)
      i.putExtra(IK_ORGANIZATION, organizationId)
      i.putExtra(IK_ACCOUNT, addingAccount)
      return i
    }
  }

  private lateinit var organizationsRepository: OrganizationsRepository
  private lateinit var sitesRepository: SitesRepository
  private var disposable: Disposable? = null

  override fun inject() {
    organizationsRepository = SyncModule.organizationsRepository
    sitesRepository = SyncModule.sitesRepository
  }

  override fun onStartCommand(
    intent: Intent,
    flags: Int,
    startId: Int
  ): Int {
    val organizationId = intent.getStringExtra(IK_ORGANIZATION)
    val siteId = intent.getStringExtra(IK_SITE)
    val addingAccount = intent.getSerializableExtra(IK_ACCOUNT) as AddingAccount


    disposable = organizationsRepository.get(organizationId)
        .flatMap { organization ->
          sitesRepository
              .get(siteId)
              .map { site ->
                val socketUrl = addingAccount.websocket
                val jobId = addingAccount.jobId
                LinkingSiteBroadcastService.newIntent(this, socketUrl, organization, site, jobId)
              }
        }
        .subscribe({
          this.startService(it)
        }) { t ->
          throw t
        }
    return Service.START_NOT_STICKY
  }

  override fun onDestroy() {
    if (disposable != null) {
      disposable!!.dispose()
    }
    super.onDestroy()
  }
}

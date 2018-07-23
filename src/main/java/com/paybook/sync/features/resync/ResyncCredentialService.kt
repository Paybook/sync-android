package com.paybook.sync.features.resync

import android.app.Service
import android.content.Context
import android.content.Intent
import com.paybook.core.BaseService
import com.paybook.core.exception.OnErrorNotImplementedException
import com.paybook.sync.SyncModule
import com.paybook.sync.data.OrganizationsRepository
import com.paybook.sync.data.SitesRepository
import com.paybook.sync.entities.AddingAccount
import com.paybook.sync.entities.credential.Credential
import com.paybook.sync.features.linkingsite.background.LinkingSiteBroadcastService
import io.reactivex.disposables.Disposable

/**
 * Created by Gerardo Teruel on 2/5/18.
 */

class ResyncCredentialService : BaseService() {

  companion object {
    const val IK_CREDENTIAL = "com.paybook.glass.home.credential"
    const val IK_ACCOUNT = "com.paybook.glass.home.addingaccount"

    fun newIntent(
      from: Context,
      credential: Credential,
      addingAccount: AddingAccount
    ): Intent {
      val i = Intent(from, ResyncCredentialService::class.java)
      i.putExtra(IK_CREDENTIAL, credential)
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
    val credential = intent.getSerializableExtra(IK_CREDENTIAL) as Credential
    val addingAccount = intent.getSerializableExtra(IK_ACCOUNT) as AddingAccount

    disposable = organizationsRepository.get(credential.organizationId)
        .flatMap { organization ->
          sitesRepository
              .get(credential.siteId)
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

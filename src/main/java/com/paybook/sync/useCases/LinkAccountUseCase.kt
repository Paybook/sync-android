package com.paybook.sync.useCases

import com.paybook.sync.SyncService
import com.paybook.sync.models.SyncResult
import com.paybook.sync.networkModels.AddAccountRequest
import com.paybook.sync.networkModels.AddAccountResponse
import io.reactivex.Single

/**
 * Created by Gerardo Teruel on 5/29/18.
 */
class LinkAccountUseCase(
  private val syncService: SyncService
) {

  fun addAccount(request: AddAccountRequest): Single<SyncResult<AddAccountResponse>> {
    return syncService.addAccount(request).map { SyncResult(it) }
  }
}

package com.paybook.sync.useCases

import com.paybook.sync.SyncService
import com.paybook.sync.models.SyncResult
import com.paybook.sync.networkModels.twofa.TwoFaRequest
import com.paybook.sync.networkModels.twofa.TwoFaResponse
import io.reactivex.Single

/**
 * Created by Gerardo Teruel on 5/30/18.
 */
class VerifyTwoFaUseCase(
  private val syncService: SyncService
) {

  fun verify(jobId: String, credentials: Map<String, String>): Single<SyncResult<TwoFaResponse>> {
    return syncService.twofa(jobId, TwoFaRequest(credentials)).map { SyncResult(it) }
  }
}

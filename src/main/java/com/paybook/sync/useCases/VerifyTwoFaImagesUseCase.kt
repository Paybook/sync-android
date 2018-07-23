package com.paybook.sync.useCases

import com.paybook.sync.SyncService
import com.paybook.sync.models.SyncResult
import com.paybook.sync.networkModels.twofa.TwoFaRequest
import com.paybook.sync.networkModels.twofa.TwoFaResponse
import io.reactivex.Observable

/**
 * Created by Gerardo Teruel on 5/30/18.
 */
class VerifyTwoFaImagesUseCase(
  private val syncService: SyncService
) {

  fun verify(jobId: String, value: Int): Observable<SyncResult<TwoFaResponse>> {
    return syncService.twofa(jobId, TwoFaRequest(mapOf("token" to value))).map { SyncResult(it) }
  }
}

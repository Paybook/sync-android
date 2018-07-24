package com.paybook.sync

import com.paybook.sync.networkModels.AddAccountRequest
import com.paybook.sync.networkModels.AddAccountResponse
import com.paybook.sync.networkModels.OrganizationsResponse
import com.paybook.sync.networkModels.twofa.TwoFaRequest
import com.paybook.sync.networkModels.twofa.TwoFaResponse
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Gerardo Teruel on 9/1/17.
 */

interface SyncService {

  @GET("/v1/catalogues/organizations/sites?is_disable=false&sort=-priority")
  fun organizations(
    @Query("is_test") isTest: Boolean?
  ): Single<Result<OrganizationsResponse>>

  @POST("/v1/credentials")
  fun addAccount(
    @Body request: AddAccountRequest
  ): Single<Result<AddAccountResponse>>

  @POST("/v1/jobs/{jobId}/twofa")
  fun twofa(
    @Path("jobId") jobId: String,
    @Body twoFaRequest: TwoFaRequest
  ): Single<Result<TwoFaResponse>>

  @PUT("/v1/credentials/{credentialId}/sync")
  fun sync(
    @Path("credentialId") credentialId: String
  ): Single<Result<AddAccountResponse>>
}

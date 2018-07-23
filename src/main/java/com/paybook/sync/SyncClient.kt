package com.paybook.sync

import com.paybook.sync.data.TokenRepository
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Gerardo Teruel on 9/1/17.
 */

class SyncClient(
  private val baseUrl: String,
  private val tokenRepository: TokenRepository,
  private val interceptor: Interceptor? = null
) {

  val service: SyncService
    get() {
      val syncService: SyncService

      val clientBuilder = OkHttpClient.Builder()
          .connectTimeout(15, TimeUnit.SECONDS)

      if (interceptor != null) clientBuilder.addNetworkInterceptor(interceptor)

      clientBuilder.addInterceptor(AuthenticationInterceptor(tokenRepository))

      val retrofit = Retrofit.Builder()
          .baseUrl(baseUrl)
          .addConverterFactory(GsonConverterFactory.create())
          .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(SyncModule.scheduler.io()))
          .client(clientBuilder.build())
          .build()
      syncService = retrofit.create(SyncService::class.java)

      return syncService
    }

  class AuthenticationInterceptor(
    private val tokenRepository: TokenRepository
  ) : Interceptor {
    override fun intercept(chain: Chain): Response {
      val request = chain.request()
          .newBuilder()
          .addHeader("Authorization", "Bearer ${tokenRepository.token()}")
          .build()
      return chain.proceed(request)
    }
  }
}

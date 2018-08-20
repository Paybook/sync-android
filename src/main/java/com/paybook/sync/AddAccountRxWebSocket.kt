package com.paybook.sync

import android.util.Log
import com.google.gson.Gson
import io.reactivex.BackpressureStrategy.BUFFER
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.EOFException

/**
 * Created by Gerardo Teruel on 10/17/17.
 */

object AddAccountRxWebSocket {


  /** Use this method to get an stream for socket events.  */
  fun stream(
    okHttpClient: OkHttpClient,
    url: String
  ): Flowable<AddAccountWebSocketResponse> {
    val request = Request.Builder()
        .get()
        .url(url)
        .build()

    return Flowable.create<AddAccountWebSocketResponse>({ e ->
      okHttpClient.newWebSocket(request, object : WebSocketListener() {
        override fun onOpen(
          webSocket: WebSocket?,
          response: Response?
        ) {
          Log.e("WS", "Connected")
        }

        override fun onMessage(
          webSocket: WebSocket?,
          text: String?
        ) {
          val gson = Gson()
          Log.e("WS", text)
          val payload = gson.fromJson(text, AddAccountWebSocketResponse::class.java)
          e.onNext(payload)
          if (payload.isTerminal()) {
            e.onComplete()
          }
        }

        override fun onClosed(
          webSocket: WebSocket?,
          code: Int,
          reason: String?
        ) {
          Log.e("WS", "Closed")
          e.onComplete()
        }

        override fun onFailure(
          webSocket: WebSocket?,
          t: Throwable?,
          response: Response?
        ) {
          Log.e("WS", "FAILED", t)
          if (t is EOFException) {
            val payload = AddAccountWebSocketResponse(code = 411)
            e.onNext(payload)
            e.onComplete()
          } else {
            e.onError(t)
          }
        }
      })

    }, BUFFER)
  }
}

package com.paybook.sync

import android.util.Log
import com.google.gson.Gson
import com.paybook.sync.AddAccountRxWebSocket.SocketEventType.MESSAGE
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

  /** The types of messages emitted by the websocket.  */
  enum class SocketEventType {
    CONNECTED,
    MESSAGE,
    END
  }

  /** The type and payload of a websocket event  */
  data class SocketEvent(
    val type: SocketEventType,
    val payload: AddAccountWebSocketResponse? = null
  )

  /** Use this method to get an stream for socket events.  */
  fun stream(
    okHttpClient: OkHttpClient,
    url: String
  ): Flowable<SocketEvent> {
    val request = Request.Builder()
        .get()
        .url(url)
        .build()

    return Flowable.create<SocketEvent>({ e ->
      okHttpClient.newWebSocket(request, object : WebSocketListener() {
        override fun onOpen(
          webSocket: WebSocket?,
          response: Response?
        ) {
          Log.e("WS", "Connected")
          e.onNext(SocketEvent(SocketEventType.CONNECTED))
        }

        override fun onMessage(
          webSocket: WebSocket?,
          text: String?
        ) {
          val gson = Gson()
          Log.e("WS", text)
          val payload = gson.fromJson(text, AddAccountWebSocketResponse::class.java)
          e.onNext(SocketEvent(SocketEventType.MESSAGE, payload))
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
            e.onNext(SocketEvent(MESSAGE, payload))
            e.onComplete()
          } else {
            e.onError(t)
          }
        }
      })

    }, BUFFER)
  }
}

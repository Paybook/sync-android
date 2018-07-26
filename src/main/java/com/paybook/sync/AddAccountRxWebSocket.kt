package com.paybook.sync

import io.reactivex.Observable
import java.io.EOFException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

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
  class SocketEvent internal constructor(
    val type: SocketEventType,
    val payload: String
  )

  /** Use this method to get an observable for socket events.  */
  fun observable(okHttpClient: OkHttpClient, url: String): Observable<SocketEvent> {
    val request = Request.Builder()
        .get()
        .url(url)
        .build()
    return Observable.create { e ->
      okHttpClient.newWebSocket(request, object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket?, response: Response?) {
          e.onNext(SocketEvent(SocketEventType.CONNECTED, ""))
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
          e.onNext(SocketEvent(SocketEventType.MESSAGE, text!!))
        }

        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
          e.onComplete()
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
          if (t !is EOFException) {
            e.onError(t)
          } else {
            e.onComplete()
          }
        }
      })
    }
  }
}

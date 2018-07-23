package com.paybook.sync.entities.credential

import com.paybook.sync.entities.credential.Credential.Status.ERROR
import com.paybook.sync.entities.credential.Credential.Status.NEEDS_ATTENTION
import com.paybook.sync.entities.credential.Credential.Status.PROCESSING
import com.paybook.sync.entities.credential.Credential.Status.UPDATED_RECENTLY
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.io.Serializable

/**
 * Created by Gerardo Teruel on 7/12/18.
 */
data class Credential(
  val id: String,
  val name: String,
  val avatar: String?,
  val authorizedAt: LocalDateTime?,
  val executedAt: LocalDateTime,
  val code: Int,
  val organizationId: String,
  val siteId: String
): Serializable {

  val status: Status
    get() {
      val now = LocalDateTime.now()
      val hours = authorizedAt?.until(now, ChronoUnit.HOURS)

      return when {
        code in 100..199 -> PROCESSING
        code in 500..599 -> ERROR
        hours == null || hours > 24 -> NEEDS_ATTENTION
        else -> UPDATED_RECENTLY
      }

    }

  enum class Status {
    PROCESSING,
    UPDATED_RECENTLY,
    NEEDS_ATTENTION,
    ERROR
  }
}

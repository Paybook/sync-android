package com.paybook.sync.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.paybook.core.Either
import com.paybook.core.fold
import com.paybook.core.imageLoader.ErrorListener
import com.paybook.core.imageLoader.ImageUrlLoader
import com.paybook.core.imageLoader.SuccessListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/**
 * Created by Gerardo Teruel on 7/18/17.
 */

class PicassoImageUrlLoader(
  private val url: String?,
  override var placeholder: Either<Drawable, Int>? = null,
  override var error: Either<Drawable, Int>? = null,
  override var onErrorListener: (() -> Unit)? = null,
  override var onSuccessListener: (() -> Unit)? = null
) : ImageUrlLoader {

  override fun load(target: ImageView) {
    val requestCreator = Picasso.get()
        .load(url)

    placeholder?.fold({ requestCreator.placeholder(it) }, { requestCreator.placeholder(it) })
    error?.fold({ requestCreator.error(it) }, { requestCreator.error(it) })
    requestCreator.into(target, object : Callback {
      override fun onSuccess() {
        onSuccessListener?.invoke()
      }

      override fun onError(e: Exception) {
        onErrorListener?.invoke()
      }
    })
  }
}
package com.paybook.sync.util

import android.util.Log
import android.webkit.URLUtil
import com.paybook.core.imageLoader.ImageUrlLoader
import com.paybook.core.imageLoader.ImageUrlLoaderFactory

/**
 * Created by Gerardo Teruel on 5/30/18.
 */
class PicassoImageUrlLoaderFactory(
  private val baseUrl: String
) : ImageUrlLoaderFactory {
  override fun create(url: String): ImageUrlLoader {
    val formattedUrl: String? =
      if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
        url
      } else if (url.isNotEmpty()) {
        baseUrl + url
      } else {
        null
      }
    Log.e("URL:", url)
    return PicassoImageUrlLoader(formattedUrl)
  }
}

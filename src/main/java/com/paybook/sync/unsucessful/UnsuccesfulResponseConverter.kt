package com.paybook.sync.unsucessful

import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException

/**
 * Created by Gerardo Teruel on 7/23/18.
 */
class UnsuccesfulResponseConverter : Converter<ResponseBody, UnsuccesfulResponse> {
  @Throws(IOException::class)
  override fun convert(value: ResponseBody): UnsuccesfulResponse {
    val gson = Gson()
    return gson.fromJson(value.string(), UnsuccesfulResponse::class.java)
  }
}


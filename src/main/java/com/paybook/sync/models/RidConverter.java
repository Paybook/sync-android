package com.paybook.sync.models;

/**
 * Created by Gerardo Teruel on 5/30/18.
 */

import android.support.annotation.NonNull;
import com.google.gson.Gson;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * A RidConverter is a very simple class that converts a network response to retrieve the rid and
 * the rid only. It assumes that the network response has a field at root level called rid.
 *
 * The main purpose of this class is debugging. In production, this method should rarely be called
 * unless Paybook's API returns an unexpected value.
 */
public class RidConverter implements Converter<ResponseBody, String> {
  @Override public String convert(@NonNull ResponseBody value) throws IOException {
    Gson gson = new Gson();
    Rid rid = gson.fromJson(value.string(), Rid.class);
    return rid.getRid();
  }
}

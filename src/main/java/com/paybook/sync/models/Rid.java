package com.paybook.sync.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gerardo Teruel on 5/30/18.
 */
public class Rid {
  @SerializedName("rid") String rid;
  @SerializedName("code") int code;
  @SerializedName("message") String info;

  public String getRid() {
    return rid;
  }
}

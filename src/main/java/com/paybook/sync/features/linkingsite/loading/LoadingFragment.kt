package com.paybook.sync.features.linkingsite.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paybook.sync.R
import com.paybook.sync.base.BaseFragment

/**
 * Created by Gerardo Teruel on 7/26/18.
 */
class LoadingFragment: BaseFragment() {

  companion object {
    fun new(): LoadingFragment {
      return LoadingFragment()
    }
  }

  override fun getFieldsFromArguments(arguments: Bundle?) {
  }

  override fun setView(
    inflater: LayoutInflater,
    container: ViewGroup?
  ): View {
    val view = inflater.inflate(R.layout.fragment_loading, container, false)

    return view
  }

  override fun inject() {
  }
}

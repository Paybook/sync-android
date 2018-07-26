package com.paybook.sync.features.linkingsite.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.paybook.core.util.clearBackStack
import com.paybook.sync.R
import com.paybook.sync.SyncModule
import com.paybook.sync.base.BaseFragment

/**
 * Created by Gerardo Teruel on 7/26/18.
 */
class SuccessFragment : BaseFragment() {

  companion object {
    fun new(): SuccessFragment {
      return SuccessFragment()
    }
  }

  lateinit var btnGoToHome: Button

  override fun getFieldsFromArguments(arguments: Bundle?) {
  }

  override fun setView(
    inflater: LayoutInflater,
    container: ViewGroup?
  ): View {
    val root = inflater.inflate(R.layout.fragment_success, container, false)
    btnGoToHome = root.findViewById(R.id.btnGoToHome)
    return root
  }

  override fun inject() {
    val i = SyncModule.homeIntentFactory.create(baseActivity)
    i.clearBackStack()
    startActivity(i)
  }
}

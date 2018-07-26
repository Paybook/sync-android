package com.paybook.sync.features.linkingsite.twofa.simpletwofa

import com.paybook.sync.R
import com.paybook.sync.base.BaseFragment
import com.paybook.sync.features.linkingsite.LinkingSiteData
import com.paybook.sync.features.linkingsite.loading.LoadingFragment
import java.lang.ref.WeakReference

/**
 * Created by Gerardo Teruel on 3/28/18.
 */

class TwoFaNavigator(base: BaseFragment) : TwoFaContract.Navigator {

  private val ref = WeakReference<BaseFragment>(base)

  override fun openLinkingSiteScreen(data: LinkingSiteData) {
    val fragment = LoadingFragment.new()
    ref.get()!!.baseActivity
        .supportFragmentManager
        .beginTransaction()
        .replace(R.id.container, fragment)
        .commit()
  }
}

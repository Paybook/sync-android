package com.paybook.sync.features.linkingsite.twofa.simpletwofa

import com.paybook.core.util.clearBackStack
import com.paybook.sync.base.BaseActivity
import com.paybook.sync.base.BaseFragment
import com.paybook.sync.features.linkingsite.LinkingSiteActivity
import com.paybook.sync.features.linkingsite.LinkingSiteData
import java.lang.ref.WeakReference

/**
 * Created by Gerardo Teruel on 3/28/18.
 */

class TwoFaNavigator(base: BaseFragment) : TwoFaContract.Navigator {

  private val ref = WeakReference<BaseFragment>(base)

  override fun openLinkingSiteScreen(data: LinkingSiteData) {
    val i = LinkingSiteActivity.newIntent(ref.get()!!.baseActivity, data)
    i.clearBackStack()
    ref.get()!!
        .startActivity(i)
  }
}

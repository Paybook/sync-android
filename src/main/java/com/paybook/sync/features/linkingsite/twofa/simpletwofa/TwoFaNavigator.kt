package com.paybook.sync.features.linkingsite.twofa.simpletwofa

import com.paybook.core.BaseActivity
import com.paybook.core.util.clearBackStack
import com.paybook.sync.features.linkingsite.LinkingSiteActivity
import com.paybook.sync.features.linkingsite.LinkingSiteData
import java.lang.ref.WeakReference

/**
 * Created by Gerardo Teruel on 3/28/18.
 */

class TwoFaNavigator(baseActivity: BaseActivity) : TwoFaContract.Navigator {

  private val activityWeakReference = WeakReference<BaseActivity>(baseActivity)

  override fun openLinkingSiteScreen(data: LinkingSiteData) {
    val i = LinkingSiteActivity.newIntent(activityWeakReference.get()!!, data)
    i.clearBackStack()
    activityWeakReference.get()!!
        .startActivity(i)
  }
}

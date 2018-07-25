package com.paybook.sync.features.linksite

import com.paybook.core.util.clearBackStack
import com.paybook.sync.base.BaseActivity
import com.paybook.sync.features.linkingsite.LinkingSiteActivity
import com.paybook.sync.features.linkingsite.LinkingSiteData
import java.lang.ref.WeakReference

/**
 * Created by Gerardo Teruel on 9/5/17.
 */

class LinkSiteNavigator(activity: BaseActivity) : LinkSiteContract.Navigator {

  private val activityWeakReference = WeakReference<BaseActivity>(activity)

  override fun openLinkingSiteScreen(linkingSiteData: LinkingSiteData) {
    val i = LinkingSiteActivity.newIntent(activityWeakReference.get()!!, linkingSiteData)
    i.clearBackStack()
    activityWeakReference.get()!!
        .startActivity(i)
  }
}

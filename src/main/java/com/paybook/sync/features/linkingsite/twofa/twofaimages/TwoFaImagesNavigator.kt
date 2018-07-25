package com.paybook.sync.features.linkingsite.twofa.twofaimages

import com.paybook.core.util.clearBackStack
import com.paybook.sync.base.BaseActivity
import com.paybook.sync.features.linkingsite.LinkingSiteActivity
import com.paybook.sync.features.linkingsite.LinkingSiteData
import com.paybook.sync.entities.LinkingSiteEvent
import java.lang.ref.WeakReference

/**
 * Created by Gerardo Teruel on 11/13/17.
 */

class TwoFaImagesNavigator(baseActivity: BaseActivity) : TwoFaImagesContract.Navigator {
  private val activityReference: WeakReference<BaseActivity> = WeakReference(baseActivity)

  override fun openLinkingSite(event: LinkingSiteEvent) {
    val data = LinkingSiteData(event.organization, event.site, event.jobId)
    val i = LinkingSiteActivity.newIntent(activityReference.get()!!, data)
    i.clearBackStack()
    activityReference.get()!!
        .startActivity(i)
  }
}
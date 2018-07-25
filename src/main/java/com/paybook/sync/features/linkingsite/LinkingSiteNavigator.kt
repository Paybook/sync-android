package com.paybook.sync.features.linkingsite

import android.content.Context
import com.paybook.core.util.clearBackStack
import com.paybook.sync.SyncModule
import com.paybook.sync.base.BaseActivity
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import com.paybook.sync.features.linksite.LinkSiteActivity
import com.paybook.sync.features.linkingsite.twofa.simpletwofa.TwoFaFragment
import com.paybook.sync.features.linkingsite.twofa.twofaimages.TwoFaImagesActivity
import java.lang.ref.WeakReference

/**
 * Created by Gerardo Teruel on 3/26/18.
 */

class LinkingSiteNavigator(
  activity: BaseActivity,
  context: Context
) : LinkingSiteContract.Navigator {

  private val activityReference: WeakReference<BaseActivity> = WeakReference(activity)
  private val contextRef: WeakReference<Context> = WeakReference(context)

  override fun openTwoFaScreen(event: LinkingSiteEvent) {
    val fragment = TwoFaFragment.newInstance(event)
    activityReference.get()!!
        .supportFragmentManager
        .beginTransaction()
        .add(fragment, null)
        .commit()
  }

  override fun openTwoFaImagesScreen(event: LinkingSiteEvent) {
    val i = TwoFaImagesActivity.newIntent(activityReference.get()!!, event)
    i.clearBackStack()
    activityReference.get()!!.startActivity(i)
  }

  override fun openHome() {
    val i = SyncModule.homeIntentFactory.create(contextRef.get()!!)
    i.clearBackStack()
    activityReference.get()!!.startActivity(i)
  }

  override fun openLinkInstitution(
    organization: Organization,
    site: Site
  ) {
    val i = LinkSiteActivity.newIntent(activityReference.get()!!, organization, site)
    i.clearBackStack()
    activityReference.get()!!.startActivity(i)
  }
}

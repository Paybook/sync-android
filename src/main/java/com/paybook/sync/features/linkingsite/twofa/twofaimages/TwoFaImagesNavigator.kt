package com.paybook.sync.features.linkingsite.twofa.twofaimages

import com.paybook.sync.R
import com.paybook.sync.base.BaseActivity
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.features.linkingsite.loading.LoadingFragment
import java.lang.ref.WeakReference

/**
 * Created by Gerardo Teruel on 11/13/17.
 */

class TwoFaImagesNavigator(baseActivity: BaseActivity) : TwoFaImagesContract.Navigator {
  private val ref: WeakReference<BaseActivity> = WeakReference(baseActivity)

  override fun openLinkingSite(event: LinkingSiteEvent) {
    val fragment = LoadingFragment.new()
    ref.get()!!
        .supportFragmentManager
        .beginTransaction()
        .replace(R.id.container, fragment)
        .commit()
  }
}
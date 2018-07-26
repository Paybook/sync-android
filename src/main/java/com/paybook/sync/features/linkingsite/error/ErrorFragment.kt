package com.paybook.sync.features.linkingsite.error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.paybook.core.util.clearBackStack
import com.paybook.sync.R
import com.paybook.sync.SyncModule
import com.paybook.sync.base.BaseFragment
import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import com.paybook.sync.features.linksite.LinkSiteActivity

/**
 * Created by Gerardo Teruel on 7/26/18.
 */
class ErrorFragment : BaseFragment() {

  companion object {
    private const val KEY_REASON = "com.paybook.sync.features.linkingsite.error.reason"
    private const val KEY_ORGANIZATION = "com.paybook.sync.features.linkingsite.error.org"
    private const val KEY_SITE = "com.paybook.sync.features.linkingsite.error.site"

    fun new(
      reason: String,
      organization: Organization,
      site: Site
    ): ErrorFragment {
      val fragment = ErrorFragment()
      val args = Bundle()
      args.putString(KEY_REASON, reason)
      args.putSerializable(KEY_ORGANIZATION, organization)
      args.putSerializable(KEY_SITE, site)
      fragment.arguments = args
      return fragment
    }
  }

  lateinit var btnGoToHome: Button
  lateinit var btnTryAgain: Button
  lateinit var txtDescription: TextView

  override fun getFieldsFromArguments(arguments: Bundle?) {
  }

  override fun setView(
    inflater: LayoutInflater,
    container: ViewGroup?
  ): View {
    val root = inflater.inflate(R.layout.fragment_error, container, false)
    btnGoToHome = root.findViewById(R.id.btnGoToHome)
    txtDescription = root.findViewById(R.id.txtDescription)
    btnTryAgain = root.findViewById(R.id.btnTryAgain)
    return root
  }

  override fun inject() {
    txtDescription.text = arguments?.getString(KEY_REASON)
    btnGoToHome.setOnClickListener {
      val i = SyncModule.homeIntentFactory.create(baseActivity)
      i.clearBackStack()
      startActivity(i)
    }

    btnTryAgain.setOnClickListener {
      val i = LinkSiteActivity.newIntent(
          baseActivity,
          arguments?.getSerializable(KEY_ORGANIZATION) as Organization,
          arguments?.getSerializable(KEY_SITE) as Site
      )
      i.clearBackStack()
      startActivity(i)
    }
  }
}

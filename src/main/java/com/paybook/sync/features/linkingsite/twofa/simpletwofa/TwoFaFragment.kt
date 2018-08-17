package com.paybook.sync.features.linkingsite.twofa.simpletwofa

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.paybook.sync.entities.twofa.TwoFaCredential
import com.paybook.sync.features.linksite.SiteCredentialsAdapter
import com.evernote.android.state.State
import com.paybook.sync.R
import com.paybook.sync.SyncModule
import com.paybook.sync.base.BaseFragment
import com.paybook.sync.useCases.VerifyTwoFaUseCase
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.Site
import io.reactivex.disposables.Disposable

/**
 * Created by Gerardo Teruel on 10/16/17.
 */

class TwoFaFragment : BaseFragment(), TwoFaContract.View {

  @State lateinit var event: LinkingSiteEvent

  private lateinit var siteNameView: TextView
  private lateinit var credentialsView: RecyclerView
  private lateinit var nextButton: Button
  private lateinit var loadingIndicator: View

  private lateinit var adapter: SiteCredentialsAdapter
  private lateinit var presenter: TwoFaContract.Presenter
  private var disposable: Disposable? = null

  companion object {

    const val IK_DATA = "com.paybook.sync.features.linkingsite.twofa.data"

    fun newInstance(event: LinkingSiteEvent): TwoFaFragment {
      val fragment = TwoFaFragment()
      val args = Bundle()
      args.putSerializable(IK_DATA, event)
      fragment.arguments = args
      return fragment
    }
  }

  override fun getFieldsFromArguments(arguments: Bundle?) {
    event = arguments!!.getSerializable(IK_DATA) as LinkingSiteEvent
  }

  override fun setView(
    inflater: LayoutInflater,
    container: ViewGroup?
  ): View {
    val root = inflater.inflate(R.layout.fragment_two_fa, container, false)
    siteNameView = root.findViewById(R.id.txtSite)
    credentialsView = root.findViewById(R.id.listCredentials)
    nextButton = root.findViewById(R.id.btnAddSite)
    loadingIndicator = root.findViewById(R.id.loadingIndicator)
    return root
  }

  override fun inject() {
    val inflater = LayoutInflater.from(context)
    adapter = SiteCredentialsAdapter(inflater)
    credentialsView.adapter = adapter
    credentialsView.layoutManager = LinearLayoutManager(context)

    nextButton.setOnClickListener { sendCredentials() }

    val view = this
    val navigator = TwoFaNavigator(this)
    val useCase = VerifyTwoFaUseCase(SyncModule.syncService)
    presenter = TwoFaPresenter(view, navigator, useCase)
  }

  private fun sendCredentials() {
    disposable = presenter.onSendCredentials(event, adapter.credentialsMap)
  }

  override fun onPause() {
    disposable?.dispose()
    super.onPause()
  }

  override fun onViewCreated() {
    showSite(event.site)
    showCredentials(event.twoFaCredentials!!)
  }

  override fun showSite(site: Site) {
    siteNameView.text = site.name
  }

  override fun showCredentials(siteCredentials: List<TwoFaCredential>) {
    adapter.show(siteCredentials)
  }

  override fun showNetworkError() {
    Snackbar.make(credentialsView, R.string.msg_network_error, Snackbar.LENGTH_SHORT)
        .show()
  }

  override fun showLoading() {
    loadingIndicator.visibility = View.VISIBLE
  }

  override fun hideLoading() {
    loadingIndicator.visibility = View.INVISIBLE
  }

  override fun showUnexpectedError(message: String) {
    Snackbar.make(credentialsView, message, Snackbar.LENGTH_LONG).show()
  }
}

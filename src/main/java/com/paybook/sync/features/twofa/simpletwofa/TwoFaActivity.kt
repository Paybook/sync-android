package com.paybook.sync.features.twofa.simpletwofa

import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.paybook.sync.entities.twofa.TwoFaCredential
import com.paybook.sync.features.linksite.SiteCredentialsAdapter
import com.evernote.android.state.State
import com.paybook.core.BaseActivity
import com.paybook.sync.R
import com.paybook.sync.SyncModule
import com.paybook.sync.useCases.VerifyTwoFaUseCase
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import io.reactivex.disposables.Disposable

/**
 * Created by Gerardo Teruel on 10/16/17.
 */

class TwoFaActivity : BaseActivity(), TwoFaContract.View {

  @State lateinit var event: LinkingSiteEvent

  private lateinit var coverView: ImageView
  private lateinit var siteNameView: TextView
  private lateinit var credentialsView: RecyclerView
  private lateinit var nextButton: Button
  private lateinit var loadingIndicator: View

  private lateinit var adapter: SiteCredentialsAdapter
  private lateinit var presenter: TwoFaContract.Presenter
  private var disposable: Disposable? = null

  companion object {

    const val IK_DATA = "com.paybook.sync.ui.twofa.data"

    fun newIntent(
      from: Context,
      event: LinkingSiteEvent
    ): Intent {
      if (event.twoFaCredentials == null) {
        throw IllegalStateException(
            "Tried to access credentials when none are available " + event.toString()
        )
      }
      val i = Intent(from, TwoFaActivity::class.java)
      i.putExtra(IK_DATA, event)
      return i
    }
  }

  override fun setView() {
    setContentView(R.layout.activity_two_fa)
    coverView = findViewById(R.id.imgCover)
    siteNameView = findViewById(R.id.txtSite)
    credentialsView = findViewById(R.id.listCredentials)
    nextButton = findViewById(R.id.btnAddSite)
    loadingIndicator = findViewById(R.id.loadingIndicator)
  }

  override fun inject() {
    if (!::event.isInitialized) {
      event = intent.getSerializableExtra(IK_DATA) as LinkingSiteEvent
    }

    val inflater = LayoutInflater.from(this)
    adapter = SiteCredentialsAdapter(inflater)
    credentialsView.adapter = adapter
    credentialsView.layoutManager = LinearLayoutManager(this)

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
    showOrganization(event.organization)
    showSite(event.site)
    showCredentials(event.twoFaCredentials!!)
  }

  override fun showOrganization(organization: Organization) {
    SyncModule.imageUrlLoaderFactory
        .create(organization.coverImageUrl)
        .load(coverView)
  }

  override fun showSite(site: Site) {
    siteNameView.text = site.name
  }

  override fun showCredentials(siteCredentials: List<TwoFaCredential>) {
    adapter.show(siteCredentials)
  }

  override fun showNetworkError() {
    Snackbar.make(coverView, R.string.msg_network_error, Snackbar.LENGTH_SHORT)
        .show()
  }

  override fun showLoading() {
    loadingIndicator.visibility = View.VISIBLE
  }

  override fun hideLoading() {
    loadingIndicator.visibility = View.INVISIBLE
  }

  override fun showUnexpectedError(message: String) {
    Snackbar.make(coverView, message, Snackbar.LENGTH_LONG).show()
  }
}

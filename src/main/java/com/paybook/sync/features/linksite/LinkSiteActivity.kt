package com.paybook.sync.features.linksite

import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.evernote.android.state.State
import com.paybook.core.BaseActivity
import com.paybook.sync.R
import com.paybook.sync.SyncModule
import com.paybook.sync.features.linkingsite.background.LinkingSiteBroadcastService
import com.paybook.sync.useCases.LinkAccountUseCase
import com.paybook.sync.entities.AddingAccount
import com.paybook.sync.entities.Credential
import com.paybook.sync.entities.LinkSiteRequest
import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site

/**
 * Created by Gerardo Teruel on 9/5/17.
 */

class LinkSiteActivity : BaseActivity(), LinkSiteContract.View {

  @State var organization: Organization? = null
  @State var site: Site? = null

  private lateinit var toolbar: Toolbar
  private lateinit var organizationCoverView: ImageView
  private lateinit var siteView: TextView
  private lateinit var credentialsView: RecyclerView
  private lateinit var linkButton: View
  private lateinit var loadingIndicator: View

  private lateinit var presenter: LinkSiteContract.Presenter
  private lateinit var adapter: SiteCredentialsAdapter

  // Lifecycle methods.
  override fun setView() {
    setContentView(R.layout.activity_link_site)
    toolbar = findViewById(R.id.toolbar)
    organizationCoverView = findViewById(R.id.imgCover)
    siteView = findViewById(R.id.txtSite)
    credentialsView = findViewById(R.id.listCredentials)
    linkButton = findViewById(R.id.btnLink)
    loadingIndicator = findViewById(R.id.loadingIndicator)
  }

  override fun inject() {
    if (organization == null) {
      organization = intent.getSerializableExtra(IK_ORGANIZATION) as Organization
    }
    if (site == null) {
      site = intent.getSerializableExtra(IK_SITE) as Site
    }

    val inflater = LayoutInflater.from(this)
    adapter = SiteCredentialsAdapter(inflater)
    credentialsView.adapter = adapter
    credentialsView.layoutManager = LinearLayoutManager(this)

    linkButton.setOnClickListener { onLink() }

    setSupportActionBar(toolbar)
    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    supportActionBar!!.setDisplayShowTitleEnabled(false)

    val view = this
    val navigator = LinkSiteNavigator(this)
    val useCase = LinkAccountUseCase(SyncModule.syncService)
    presenter = LinkSitePresenter(view, navigator, useCase, organization!!, site!!)
  }

  override fun onViewCreated() {
    presenter.subscribe()
  }

  // Callbacks
  private fun onLink() {
    val request =
      LinkSiteRequest(organization!!, site!!, adapter.credentialsMap)
    presenter.link(request)
  }

  // View methods.
  override fun showOrganization(organization: Organization) {
    this.organization = organization
    SyncModule.imageUrlLoaderFactory
        .create(organization.coverImageUrl)
        .load(organizationCoverView)
  }

  override fun showSite(site: Site) {
    this.site = site
    siteView.text = site.name
    adapter.show(site.credentials as List<Credential>)
  }

  override fun startLinkingAccount(addingAccount: AddingAccount) {
    val webSocket = addingAccount.websocket
    val jobId = addingAccount.jobId
    val i = LinkingSiteBroadcastService.newIntent(this, webSocket, organization!!, site!!, jobId)
    startService(i)
  }

  override fun showError(message: String) {
    Snackbar.make(credentialsView, message, Snackbar.LENGTH_LONG).show()
  }

  override fun showNetworkError() {
    Snackbar.make(credentialsView, R.string.msg_network_error, Snackbar.LENGTH_SHORT).show()
  }

  override fun showLoadingIndicator() {
    loadingIndicator.visibility = View.VISIBLE
  }

  override fun hideLoadingIndicator() {
    loadingIndicator.visibility = View.INVISIBLE
  }

  override fun showAccountConnected() {
    organizationCoverView.visibility = View.INVISIBLE
    credentialsView.visibility = View.INVISIBLE
    linkButton.visibility = View.INVISIBLE
  }

  companion object {

    private const val IK_ORGANIZATION = "com.paybook.paybook.linksite.organization"
    private const val IK_SITE = "com.paybook.paybook.linksite.site"

    // Creational methods.
    fun newIntent(
      from: Context,
      organization: Organization,
      site: Site
    ): Intent {
      val i = Intent(from, LinkSiteActivity::class.java)
      i.putExtra(IK_ORGANIZATION, organization)
      i.putExtra(IK_SITE, site)
      return i
    }
  }
}

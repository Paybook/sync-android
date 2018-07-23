package com.paybook.sync.features.twofa.twofaimages

import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.evernote.android.state.State
import com.paybook.core.BaseActivity
import com.paybook.sync.R
import com.paybook.sync.SyncModule
import com.paybook.sync.features.twofa.twofaimages.adapter.ImageSelectedListener
import com.paybook.sync.features.twofa.twofaimages.adapter.TwoFaImagesAdapter
import com.paybook.sync.useCases.VerifyTwoFaImagesUseCase
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.Organization
import com.paybook.sync.entities.Site
import com.paybook.sync.entities.twofa.TwoFaImage
import io.reactivex.disposables.Disposable

/**
 * Created by Gerardo Teruel on 11/13/17.
 */

class TwoFaImagesActivity : BaseActivity(), TwoFaImagesContract.View {
  @State var event: LinkingSiteEvent? = null

  private lateinit var coverView: ImageView
  private lateinit var siteNameView: TextView
  private lateinit var credentialsView: RecyclerView
  private lateinit var nextButton: Button
  private lateinit var loadingIndicator: View

  lateinit var presenter: TwoFaImagesContract.Presenter
  lateinit var adapter: TwoFaImagesAdapter
  private var submitImageDisposable: Disposable? = null

  // Creational methods
  companion object {
    const val IK_DATA = "com.paybook.sync.ui.twofa.twofaimages.data"

    fun newIntent(
      from: Context,
      event: LinkingSiteEvent
    ): Intent {
      if (event.twoFaImages == null) {
        throw IllegalStateException(
            "Tried to open image selection when none is available " + event.toString()
        )
      }
      val i = Intent(from, TwoFaImagesActivity::class.java)
      i.putExtra(IK_DATA, event)
      return i
    }
  }

  // Lifecycle methods.
  override fun setView() {
    setContentView(R.layout.activity_two_fa)
    coverView = findViewById(R.id.imgCover)
    siteNameView = findViewById(R.id.txtSite)
    credentialsView = findViewById(R.id.listCredentials)
    nextButton = findViewById(R.id.btnAddSite)
    loadingIndicator = findViewById(R.id.loadingIndicator)
  }

  override fun inject() {
    if (event == null) {
      event = intent.getSerializableExtra(IK_DATA) as LinkingSiteEvent
    }

    nextButton.setOnClickListener { onSubmitImage() }

    adapter = TwoFaImagesAdapter(
        object : ImageSelectedListener {
          override fun onImageSelected(twoFaImage: TwoFaImage) {
            presenter.onImageSelected(twoFaImage)
          }
        },
        object : CannotReadFileListener {
          override fun cantReadFile() {
            presenter.onFileCantBeLoaded()
          }
        })

    credentialsView.layoutManager = GridLayoutManager(this, 2)
    credentialsView.adapter = adapter

    val navigator = TwoFaImagesNavigator(this)
    val useCase = VerifyTwoFaImagesUseCase(SyncModule.syncService)
    presenter = TwoFaImagesPresenter(this, navigator, useCase)
  }

  override fun onResume() {
    super.onResume()
    presenter.subscribe(event!!)
  }

  override fun onPause() {
    submitImageDisposable?.dispose()
    super.onPause()
  }

  // Callbacks.
  private fun onSubmitImage() {
    val twoFaImage = adapter.selectedValue
    if (twoFaImage == null) {
      showPleaseSelectImageMessage()
    } else {
      submitImageDisposable = presenter.submitImage(event!!, adapter.selectedValue!!)
    }
  }

  // View methods
  override fun show(event: LinkingSiteEvent) {
    this.event = event
    showOrganization(event.organization)
    showSite(event.site)
    showTwoFaImages(event.twoFaImages!!)
  }

  private fun showOrganization(organization: Organization) {
    SyncModule.imageUrlLoaderFactory
        .create(organization.coverImageUrl)
        .load(coverView)
  }

  private fun showSite(site: Site) {
    siteNameView.text = site.name
  }

  private fun showTwoFaImages(files: List<TwoFaImage>) {
    adapter.show(files)
  }

  override fun showNetworkError() {
    Snackbar.make(coverView, R.string.msg_network_error, Snackbar.LENGTH_SHORT)
        .show()
  }

  override fun showUnexpectedError() {
    Snackbar.make(coverView, R.string.msg_unexpected_error, Snackbar.LENGTH_SHORT)
        .show()
  }

  override fun showLoading() {
    loadingIndicator.visibility = View.VISIBLE
  }

  override fun hideLoading() {
    loadingIndicator.visibility = View.INVISIBLE
  }

  override fun showPleaseSelectImageMessage() {
    Snackbar.make(loadingIndicator, R.string.msg_please_select_image, Snackbar.LENGTH_SHORT)
        .show()
  }

  override fun showImageSelected(twoFaImage: TwoFaImage) {
    adapter.select(twoFaImage)
  }

}

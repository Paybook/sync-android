package com.paybook.sync.features.linkingsite.twofa.twofaimages

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.evernote.android.state.State
import com.paybook.sync.R
import com.paybook.sync.SyncModule
import com.paybook.sync.base.BaseFragment
import com.paybook.sync.entities.LinkingSiteEvent
import com.paybook.sync.entities.Site
import com.paybook.sync.entities.twofa.TwoFaImage
import com.paybook.sync.features.linkingsite.twofa.twofaimages.adapter.ImageSelectedListener
import com.paybook.sync.features.linkingsite.twofa.twofaimages.adapter.TwoFaImagesAdapter
import com.paybook.sync.useCases.VerifyTwoFaImagesUseCase
import io.reactivex.disposables.Disposable

/**
 * Created by Gerardo Teruel on 11/13/17.
 */

class TwoFaImagesFragment : BaseFragment(), TwoFaImagesContract.View {
  @State var event: LinkingSiteEvent? = null

  private lateinit var siteNameView: TextView
  private lateinit var credentialsView: RecyclerView
  private lateinit var nextButton: Button
  private lateinit var loadingIndicator: View
  private lateinit var txtDescription: TextView

  lateinit var presenter: TwoFaImagesContract.Presenter
  lateinit var adapter: TwoFaImagesAdapter
  private var submitImageDisposable: Disposable? = null

  // Creational methods
  companion object {
    const val IK_DATA = "com.paybook.sync.ui.twofa.twofaimages.data"

    fun new(
      event: LinkingSiteEvent
    ): TwoFaImagesFragment {
      val fragment = TwoFaImagesFragment()
      val args = Bundle()
      args.putSerializable(IK_DATA, event)
      fragment.arguments = args
      return fragment
    }
  }

  // Lifecycle methods.
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
    txtDescription = root.findViewById(R.id.txtDescription)
    return root
  }


  override fun inject() {
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

    credentialsView.layoutManager = GridLayoutManager(baseActivity, 2)
    credentialsView.adapter = adapter

    val navigator = TwoFaImagesNavigator(baseActivity)
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
    txtDescription.text = event.label
    showSite(event.site)
    showTwoFaImages(event.twoFaImages!!)
  }

  private fun showSite(site: Site) {
    siteNameView.text = site.name
  }

  private fun showTwoFaImages(files: List<TwoFaImage>) {
    adapter.show(files)
  }

  override fun showNetworkError() {
    Snackbar.make(credentialsView, R.string.msg_network_error, Snackbar.LENGTH_SHORT)
        .show()
  }

  override fun showUnexpectedError() {
    Snackbar.make(credentialsView, R.string.msg_unexpected_error, Snackbar.LENGTH_SHORT)
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

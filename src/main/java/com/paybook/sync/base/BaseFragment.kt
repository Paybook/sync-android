package com.paybook.sync.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evernote.android.state.StateSaver

/**
 * Created by Gerardo Teruel on 5/14/18.
 */
abstract class BaseFragment : Fragment() {

  protected lateinit var root: View

  /**
   * Returns the base activity.
   *
   * @return the base activity that holds this fragment.
   * @throws ClassCastException if the parent activity doesn't inherit from BaseActivity.
   */
  val baseActivity: BaseActivity
    get() = activity as BaseActivity

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    StateSaver.restoreInstanceState(this, savedInstanceState)
    getFieldsFromArguments(arguments)
    return setView(inflater, container)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    inject()
    onViewCreated()
  }

  override fun onDestroyView() {
    onViewDestroyed()
    super.onDestroyView()
  }

  /**
   * Init all fields with the arguments set to the fragment. Make sure the fields use the @State
   * annotation for they to persist config changes. Each field will be restored from instance
   * state, so before setting it from arguments check if it's null.
   *
   * @param arguments - The arguments with which the fragment was initialized.
   */
  protected abstract fun getFieldsFromArguments(arguments: Bundle?)

  /**
   * Set the view for the Fragment, and return it.
   *
   * @param inflater - as provided by onCreateView, use to inflate layout.
   * @param container - as provided by oncreateView, inflate layout into this container.
   */
  protected abstract fun setView(
    inflater: LayoutInflater,
    container: ViewGroup?
  ): View

  /**
   * Inject all fields needed by the fragment.
   * Optional, set the lifecycleHandler instance if you need custom logic to be implemented at
   * different lifecycle events.
   */
  protected abstract fun inject()

  /**
   * Callback method, called when the view is created and injected. Typically, this method
   * will be called to subscribe to presenters. A call to super is not necessary.
   */
  protected open fun onViewCreated() {

  }

  /**
   * Callback method, called when the view is about to be destroyed. Typically, this method
   * will be called to unsubscribe to presenters. A call to super is not necessary.
   */
  protected open fun onViewDestroyed() {

  }

  @CallSuper override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    StateSaver.saveInstanceState(this, outState)
  }
}

package com.paybook.sync.base

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager

/**
 * Created by Gerardo Teruel on 4/30/18.
 */
abstract class BaseActivity : AppCompatActivity() {

  protected abstract fun setView()
  protected abstract fun inject()

  protected open fun onViewCreated() {

  }

  protected open fun onViewDestroyed() {

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setView()
    inject()
    onViewCreated()
  }

  override fun onDestroy() {
    onViewDestroyed()
    super.onDestroy()
  }

  override fun onBackPressed() {
    super.onBackPressed()
    val view = this.currentFocus
    if (view != null) {
      val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
  }
}
package com.paybook.sync.features.linksite

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.paybook.sync.R
import com.paybook.sync.entities.Credential
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Gerardo Teruel on 9/5/17.
 */

class SiteCredentialsAdapter(private val inflater: LayoutInflater) : RecyclerView.Adapter<SiteCredentialsAdapter.SiteCredentialViewHolder>() {

  val credentials: MutableList<Credential> = ArrayList()
  val credentialsMap: MutableMap<String, String> = HashMap()

  fun show(credentials: List<Credential>) {
    this.credentials.clear()
    this.credentials.addAll(credentials)
    this.notifyDataSetChanged()
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): SiteCredentialViewHolder {
    return SiteCredentialViewHolder.inflate(inflater, parent, credentialsMap)
  }

  override fun onBindViewHolder(
    holder: SiteCredentialViewHolder,
    position: Int
  ) {
    holder.bind(credentials[position])
  }

  override fun getItemCount(): Int {
    return credentials.size
  }

  class SiteCredentialViewHolder private constructor(
    itemView: View,
    credentials: MutableMap<String, String>
  ) : RecyclerView.ViewHolder(itemView) {

    private var credential: Credential? = null

    private val hintView: TextView = itemView.findViewById(R.id.txtHint)
    private val credentialInput: TextView = itemView.findViewById(R.id.txtSiteCredential)

    init {

      // Update credentialsMap value.
      credentialInput.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(
          s: CharSequence,
          start: Int,
          count: Int,
          after: Int
        ) {

        }

        override fun onTextChanged(
          s: CharSequence,
          start: Int,
          before: Int,
          count: Int
        ) {

        }

        override fun afterTextChanged(s: Editable) {
          credentials[credential!!.name] = s.toString()
        }
      })
    }

    fun bind(credential: Credential) {
      this.credential = credential
      hintView.text = credential.label

      // Set input type dynamically.
      if (credential.type != "text") {
        credentialInput.inputType = InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_PASSWORD
      } else {
        credentialInput.inputType = InputType.TYPE_CLASS_TEXT
      }
    }

    companion object {

      fun inflate(
        inflater: LayoutInflater,
        parent: ViewGroup,
        credentials: MutableMap<String, String>
      ): SiteCredentialViewHolder {
        val v = inflater.inflate(R.layout.item_site_credential, parent, false)
        return SiteCredentialViewHolder(v, credentials)
      }
    }
  }
}

package com.paybook.sync.features.linkingsite.twofa.twofaimages.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.paybook.sync.features.linkingsite.twofa.twofaimages.CannotReadFileListener
import com.paybook.sync.entities.twofa.TwoFaImage
import java.util.ArrayList

/**
 * Created by Gerardo Teruel on 11/13/17.
 */

class TwoFaImagesAdapter(
  private val imageSelectedListener: ImageSelectedListener,
  private val cannotReadFileListener: CannotReadFileListener
) : RecyclerView.Adapter<TwoFaImageViewHolder>() {

  private val twoFaImages: MutableList<SelectableTwoFaImage>

  /**
   * Return the selected file or null if nothing is selected.
   * @throws IllegalStateException if there's more than one file selected.
   * @return the selected file with value or null if nothing is selected
   */
  val selectedValue: TwoFaImage?
    get() {
      val filtered = twoFaImages.filter { it.isSelected }
      return when {
        filtered.isEmpty() -> null
        filtered.size == 1 -> filtered[0].twoFaImage
        else -> throw IllegalStateException("More than one file is selected")
      }
    }

  init {
    this.twoFaImages = ArrayList()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TwoFaImageViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return TwoFaImageViewHolder.inflate(inflater, parent, imageSelectedListener)
  }

  override fun onBindViewHolder(holder: TwoFaImageViewHolder, position: Int) {
    holder.bind(twoFaImages[position])
  }

  override fun getItemCount(): Int {
    return twoFaImages.size
  }

  fun show(twoFaImages: List<TwoFaImage>) {
    this.twoFaImages.clear()
    this.twoFaImages.addAll(twoFaImages.map { SelectableTwoFaImage(false, it) })
    notifyDataSetChanged()
  }

  fun select(twoFaImage: TwoFaImage) {
    for (fileWithValue in twoFaImages) {
      fileWithValue.isSelected = false
    }
    val pos = twoFaImages.indexOf(SelectableTwoFaImage(false, twoFaImage))
    twoFaImages[pos].isSelected = true

    notifyDataSetChanged()
  }
}

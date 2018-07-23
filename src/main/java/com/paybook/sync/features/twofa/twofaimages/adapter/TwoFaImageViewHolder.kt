package com.paybook.sync.features.twofa.twofaimages.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.paybook.core.imageLoader.ImageUrlLoaderFactory
import com.paybook.sync.R
import com.paybook.sync.SyncModule

/**
 * Created by Gerardo Teruel on 11/13/17.
 */

class TwoFaImageViewHolder private constructor(
  itemView: View,
  imageSelectedListener: ImageSelectedListener
) : RecyclerView.ViewHolder(itemView) {

  private var selectableTwoFaImage: SelectableTwoFaImage? = null
  private val imageUrlLoaderFactory: ImageUrlLoaderFactory = SyncModule.imageUrlLoaderFactory
  private val imageView: ImageView = itemView.findViewById(R.id.imgTwoFa)

  init {
    this.itemView.setOnClickListener {
      imageSelectedListener.onImageSelected(selectableTwoFaImage!!.twoFaImage)
    }
  }

  fun bind(selectableTwoFaImage: SelectableTwoFaImage) {
    this.selectableTwoFaImage = selectableTwoFaImage
    imageUrlLoaderFactory.create(selectableTwoFaImage.twoFaImage.uri)
        .load(imageView)
    imageView.alpha = if (selectableTwoFaImage.isSelected) 1.0f else 0.6f
  }

  companion object {

    fun inflate(
      inflater: LayoutInflater,
      parent: ViewGroup,
      imageSelectedListener: ImageSelectedListener
    ): TwoFaImageViewHolder {
      val v = inflater.inflate(R.layout.item_two_fa_image, parent, false)
      return TwoFaImageViewHolder(v, imageSelectedListener)
    }
  }
}

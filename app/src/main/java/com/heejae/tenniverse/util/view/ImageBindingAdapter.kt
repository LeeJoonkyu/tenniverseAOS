package com.heejae.tenniverse.util.view

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.heejae.tenniverse.R

private val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

private val tenniverseSampleImgList = listOf(
    R.drawable.tenniverse_sample_1,
    R.drawable.tenniverse_sample_2,
    R.drawable.tenniverse_sample_3,
    R.drawable.tenniverse_sample_4,
    R.drawable.tenniverse_sample_5,
    R.drawable.tenniverse_sample_6,
    R.drawable.tenniverse_sample_7,
    R.drawable.tenniverse_sample_8,
    R.drawable.tenniverse_sample_9,
    R.drawable.tenniverse_sample_10,
)

@BindingAdapter(value = ["setGlide", "setUri"])
fun ImageView.setImg(glide: RequestManager, uri: String?) {
    glide.load(uri)
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .fallback(R.color.light_gray)
        .transform(CenterCrop(), RoundedCorners(36))
        .into(this)
}

@BindingAdapter(value = ["setGlide", "setIdx"])
fun ImageView.setImg(glide: RequestManager, idx: Int) {
    glide.load(tenniverseSampleImgList[idx])
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .fallback(R.color.light_gray)
        .transform(CenterCrop(), RoundedCorners(36))
        .into(this)
}
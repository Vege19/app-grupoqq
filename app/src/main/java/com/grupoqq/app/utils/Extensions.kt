package com.grupoqq.app.utils

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun View.makeVisible() {
    this.visibility = View.VISIBLE
}

fun View.makeGone() {
    this.visibility = View.GONE
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun ImageView.setGlideImage(context: Context, url: String, circleCrop: Boolean = false) {
    if (circleCrop) {
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()).into(this)
    } else {
        Glide.with(context).load(url).into(this)
    }
}


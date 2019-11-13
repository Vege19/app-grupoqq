package com.grupoqq.app.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

fun getFirebaseReference(path: String): DatabaseReference {
    return FirebaseDatabase.getInstance().getReference(path)
}

fun ImageView.setGlideImage(context: Context, url: String) {
    Glide.with(context)
        .load(url)
        .into(this)
}

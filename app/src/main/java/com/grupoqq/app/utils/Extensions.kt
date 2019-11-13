package com.grupoqq.app.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

fun getFirebaseReference(path: String): DatabaseReference {
    return FirebaseDatabase.getInstance().getReference(path)
}
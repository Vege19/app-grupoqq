package com.grupoqq.app.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.grupoqq.app.R
import com.grupoqq.app.models.QuotationModel
import com.grupoqq.app.models.SparePartModel
import java.text.SimpleDateFormat
import java.util.*

fun View.makeVisible() {
    this.visibility = View.VISIBLE
}

fun View.makeGone() {
    this.visibility = View.GONE
}

fun showToast(context: Context, message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, length).show()
}

fun ImageView.setGlideImage(context: Context, url: String, circleCrop: Boolean = false) {
    if (circleCrop) {
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()).into(this)
    } else {
        Glide.with(context).load(url).into(this)
    }
}

fun getDateTime(): String {
    val date = Calendar.getInstance().time
    val formatter = SimpleDateFormat.getDateTimeInstance()
    return formatter.format(date)
}

fun updateQuotation(binnacleId: String, quotationModel: QuotationModel) {
    val quotationList = mutableListOf<QuotationModel>()

    FirebaseDatabase.getInstance().getReference("binnacles").child(binnacleId).child("quotation").addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Log.d("debug", p0.message)
        }

        override fun onDataChange(p0: DataSnapshot) {

            if (p0.exists()) {
                if (quotationList.size > 0) quotationList.clear()

                for (tmp in p0.children) {
                    val quotation = tmp.getValue(QuotationModel::class.java)
                    quotationList.add(quotation!!)
                }
            }
            quotationList.add(quotationModel)
            FirebaseDatabase.getInstance().getReference("binnacles").child(binnacleId).child("quotation").setValue(quotationList)
        }

    })

}

fun addSparePartToQuotation(binnacleId: String, binnacleServiceId: String) {
    var sparePart = SparePartModel()
    FirebaseDatabase.getInstance().getReference("binnacles").child(binnacleId).child("binnacleServices").child(binnacleServiceId).child("binnacleServiceSpareParts").addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Log.d("debug", p0.message)
        }

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {
                for (tmp in p0.children) {
                    sparePart = tmp.getValue(SparePartModel::class.java)!!
                    updateQuotation(binnacleId, QuotationModel(sparePart.sparePartName, sparePart.sparePartPrice))
                }
            }
        }

    })
}



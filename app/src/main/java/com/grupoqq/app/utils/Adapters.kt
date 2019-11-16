package com.grupoqq.app.utils

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.grupoqq.app.R
import com.grupoqq.app.models.MechanicModel
import com.grupoqq.app.models.ServiceModel
import kotlinx.android.synthetic.main.item_mechanic.view.*
import kotlinx.android.synthetic.main.item_service.view.*


fun MechanicAdapter(mechanics: List<MechanicModel>, context: Context, imageView: ImageView, textView: TextView, bottomSheet: BottomSheetDialog): GenericAdapter<MechanicModel> {
    return GenericAdapter(R.layout.item_mechanic, mechanics, fun (viewHolder, view, mechanic, _) {
        //Set data
        view.itemMechanicImg.setGlideImage(context, mechanic.mechanicPhoto, true)
        view.itemMechanicNameTxt.text = mechanic.mechanicNames
        viewHolder.itemView.setOnClickListener {
            imageView.makeVisible()
            imageView.setGlideImage(context, mechanic.mechanicPhoto, true)
            textView.text = mechanic.mechanicNames
            bottomSheet.dismiss()
        }
    })
}

fun ServiceAdapter(services: List<ServiceModel>, context: Context): GenericAdapter<ServiceModel> {
    return GenericAdapter(R.layout.item_service, services, fun (viewHolder, view, service, _) {
        view.itemServiceNameTxt.text = service.serviceName
        if (service.isChecked) {
            view.itemServiceCheckBox.isChecked = service.isChecked
        } else {
            view.itemServiceCheckBox.isChecked = service.isChecked
        }
    })
}
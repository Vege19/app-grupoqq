package com.grupoqq.app.utils

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.grupoqq.app.R
import com.grupoqq.app.activities.NewBinnacleActivity
import com.grupoqq.app.models.*
import kotlinx.android.synthetic.main.item_binnacle.view.*
import kotlinx.android.synthetic.main.item_client.view.*
import kotlinx.android.synthetic.main.item_mechanic.view.*
import kotlinx.android.synthetic.main.item_service.view.*
import kotlinx.android.synthetic.main.item_vehicle.view.*


fun MechanicAdapter(mechanics: List<MechanicModel>, context: Context, imageView: ImageView?, textView: TextView?, bottomSheet: BottomSheetDialog?): GenericAdapter<MechanicModel> {
    return GenericAdapter(R.layout.item_mechanic, mechanics, fun (viewHolder, view, mechanic, _) {
        //Set data
        view.itemMechanicImg.setGlideImage(context, mechanic.mechanicPhoto, true)
        view.itemMechanicNameTxt.text = mechanic.mechanicNames
        viewHolder.itemView.setOnClickListener {
            if (imageView != null && textView != null) {
                imageView.makeVisible()
                imageView.setGlideImage(context, mechanic.mechanicPhoto, true)
                textView.text = mechanic.mechanicNames
            }
            NewBinnacleActivity.newMechanic = mechanic
            NewBinnacleActivity.isMechanicSelected = true
            bottomSheet?.dismiss()
        }
    })
}

fun ServiceAdapter(services: List<ServiceModel>, selectedServices: MutableList<ServiceModel>, context: Context): GenericAdapter<ServiceModel> {
    return GenericAdapter(R.layout.item_service, services, fun (viewHolder, view, service, _) {
        view.itemServiceNameTxt.text = service.serviceName

        view.itemServiceCheckBox.setOnClickListener {
            if (view.itemServiceCheckBox.isChecked) {
                Log.d("Debug", "${service.serviceName} is checked")
                selectedServices.add(service)
                /*service.isChecked = false
                if (service.isChecked) {
                    selectedServices.add(service)
                } else {
                    selectedServices.remove(service)
                }*/
            } else if (!view.itemServiceCheckBox.isChecked){
                Log.d("Debug", "${service.serviceName} is unchecked")
                selectedServices.remove(service)
                /*service.isChecked= true
                if (service.isChecked) {
                    selectedServices.add(service)
                } else {
                    selectedServices.remove(service)
                }*/
            }
        }
    })
}

fun BinnacleAdapter(binnacles: List<BinnacleModel>): GenericAdapter<BinnacleModel> {
    return GenericAdapter(R.layout.item_binnacle, binnacles, fun (viewHolder, view, binnacle, _) {
        view.itemBinnacleIdTxt.text = binnacle.binnacleId
        view.itemBinnacleClientNameTxt.text = "Cliente: ${binnacle.client.clientNames}"
    })
}

fun ClientAdapter(clients: List<ClientModel>, context: Context): GenericAdapter<ClientModel> {
    return GenericAdapter(R.layout.item_client, clients, fun (viewHolder, view, client, _) {
        view.itemClientNameTxt.text = client.clientNames
    })
}

fun VehicleAdapter(vehicles: List<VehicleModel>, context: Context): GenericAdapter<VehicleModel> {
    return GenericAdapter(R.layout.item_vehicle, vehicles, fun (viewHolder, view, vehicle, _) {
        view.itemVehicleNameTxt.text = "${vehicle.vehicleBrand} ${vehicle.vehicleModel} ${vehicle.vehicleYear}"
        view.itemVehicleRegistrationNumberTxt.text = "Matrícula: ${vehicle.vehicleRegistrationNumber}"
    })
}
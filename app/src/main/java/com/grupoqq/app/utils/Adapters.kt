package com.grupoqq.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.grupoqq.app.R
import com.grupoqq.app.activities.BinnacleActivity
import com.grupoqq.app.activities.NewBinnacleActivity
import com.grupoqq.app.activities.ReportsActivity
import com.grupoqq.app.models.*
import kotlinx.android.synthetic.main.item_binnacle.view.*
import kotlinx.android.synthetic.main.item_binnacle_service.view.*
import kotlinx.android.synthetic.main.item_client.view.*
import kotlinx.android.synthetic.main.item_mechanic.view.*
import kotlinx.android.synthetic.main.item_report.view.*
import kotlinx.android.synthetic.main.item_service.view.*
import kotlinx.android.synthetic.main.item_sparepart.view.*
import kotlinx.android.synthetic.main.item_vehicle.view.*
import kotlinx.android.synthetic.main.sheet_new_report.view.*


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

fun BinnacleAdapter(binnacles: List<BinnacleModel>, context: Context, isMechanic: Boolean = false): GenericAdapter<BinnacleModel> {
    return GenericAdapter(R.layout.item_binnacle, binnacles, fun (viewHolder, view, binnacle, _) {
        view.itemBinnacleIdTxt.text = binnacle.binnacleId
        view.itemBinnacleClientNameTxt.text = "Cliente: ${binnacle.client.clientNames}"
        viewHolder.itemView.setOnClickListener {
            context.startActivity(Intent(context, BinnacleActivity::class.java).putExtra("BINNACLE_KEY", binnacle.binnacleId).putExtra("IS_MECHANIC", isMechanic))
        }
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

fun BinnacleServiceAdapter(binnacleServices: List<BinnacleServiceModel>, context: Context, isMechanic: Boolean = false): GenericAdapter<BinnacleServiceModel> {
    return GenericAdapter(R.layout.item_binnacle_service, binnacleServices, fun (viewHolder, view, binnacleService, _) {
        view.itemBinnacleServiceNameTxt.text = binnacleService.service.serviceName
        //status color
        when (binnacleService.binnacleServiceStatus) {
            1 -> view.itemBinnacleServiceStatusTxt.setTextColor(ContextCompat.getColor(context, R.color.colorLight))
            2 -> view.itemBinnacleServiceStatusTxt.setTextColor(Color.RED)
            3 -> view.itemBinnacleServiceStatusTxt.setTextColor(Color.GREEN)
            else -> view.itemBinnacleServiceStatusTxt.setTextColor(Color.GRAY)
        }
        //status text
        view.itemBinnacleServiceStatusTxt.text = when (binnacleService.binnacleServiceStatus) {
            1 -> "Pendiente de revisión"
            2 -> "Pendiente de aprobación"
            3 -> "En proceso"
            else -> "Finalizado"
        }
        //Intent
        viewHolder.itemView.setOnClickListener {
            if (binnacleService.binnacleServiceStatus != 1 || isMechanic) {
                val intent = Intent(context, ReportsActivity::class.java)
                intent.putExtra("BINNACLE_SERVICE_KEY", binnacleService.binnacleServiceId)
                context.startActivity(intent)
            } else {
                showToast(context, "Este servicio se encuentra aún en revisión por parte del mecánico", Toast.LENGTH_LONG)
            }
        }
    })
}

fun SparePartAdapter(spareParts: List<SparePartModel>, context: Context, isMechanic: Boolean = false, selectedSpareParts: MutableList<SparePartModel>): GenericAdapter<SparePartModel> {
    return GenericAdapter(R.layout.item_sparepart, spareParts, fun (viewHolder, view, sparePart, _) {
        view.itemSparePartImg.setGlideImage(context, sparePart.sparePartPhoto)
        view.itemSparePartNameTxt.text = sparePart.sparePartName
        view.itemSparePartPriceTxt.text = "$${sparePart.sparePartPrice}"

        if (isMechanic) {
            view.itemSparePartCheckBox.makeVisible()
            view.itemSparePartCheckBox.setOnClickListener {
                if (view.itemSparePartCheckBox.isChecked) {
                    Log.d("Debug", "${sparePart.sparePartName} isChecked")
                    selectedSpareParts.add(sparePart)
                } else {
                    Log.d("Debug", "${sparePart.sparePartName} isUnchecked")
                    selectedSpareParts.remove(sparePart)
                }
            }
        }

    })
}

fun ReportAdapter(reports: List<ReportModel>, context: Context): GenericAdapter<ReportModel> {
    return GenericAdapter(R.layout.item_report, reports, fun (viewHolder, view, report, _) {
        view.itemReportDescriptionTxt.text = report.reportDescription
        view.itemReportDateTxt.text = report.reportDateTime
        Log.d("debug", "Uri path: " + report.reportPhoto)
        //view.itemReportImg.setImageURI(Uri.parse(report.reportPhoto))
        Glide.with(context).load(Uri.parse(report.reportPhoto)).into(view.itemReportImg)
        //view.itemReportImg.setGlideImage(context, report.reportPhoto)
    })

}
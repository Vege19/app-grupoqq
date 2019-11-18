package com.grupoqq.app.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.grupoqq.app.R
import com.grupoqq.app.activities.BinnacleActivity
import com.grupoqq.app.models.BinnacleModel
import com.grupoqq.app.models.QuotationModel
import com.grupoqq.app.utils.*
import kotlinx.android.synthetic.main.activity_new_binnacle.*
import kotlinx.android.synthetic.main.fragment_binnacle_details.*

class BinnacleDetailsFragment : Fragment() {

    private var binnaclesReferences = FirebaseDatabase.getInstance().getReference("binnacles")
    private lateinit var binnacle: BinnacleModel
    private var quotationList = mutableListOf<QuotationModel>()
    private lateinit var quotationAdapter: GenericAdapter<QuotationModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_binnacle_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getBinnacle()
        quotationListRecyclerViewSetup()
        getQuotationList()

    }

    private fun getBinnacle() {
        binnaclesReferences.child(BinnacleActivity.binnacleId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showToast(requireContext(), p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    binnacle = p0.getValue(BinnacleModel::class.java)!!
                    setBinnacleViews()
                }
            }
        })
    }

    private fun setBinnacleViews() {
        //Binnacle info
        binnacleDetailsCodeTxt.text = "Referencia: " + binnacle.binnacleId
        //Vehicle info
        binnacleDetailsBrandTxt.text = "Marca: " + binnacle.vehicle.vehicleBrand
        binnacleDetailsModelTxt.text = "Modelo: " + binnacle.vehicle.vehicleModel
        binnacleDetailsYearTxt.text = "Año: " + binnacle.vehicle.vehicleYear
        binnacleDetailsRegistrationNumberTxt.text = "Matrícula: " + binnacle.vehicle.vehicleRegistrationNumber
        //Client info
        binnacleDetailsClientNameTxt.text = "Nombre: " + binnacle.client.clientNames
        binnacleDetailsClientEmailTxt.text = "Correo electrónico: " + binnacle.client.clientEmail
        binnacleDetailsClientPhoneTxt.text = "Teléfono: " + binnacle.client.clientPhone
        //Mechanic info
        if (BinnacleActivity.isMechanic) {
            cardView4.makeGone()
        } else {
            binnacleDetailsMechanicImg.setGlideImage(requireContext(), binnacle.mechanic.mechanicPhoto, true)
            binnacleDetailsMechanicNameTxt.text = binnacle.mechanic.mechanicNames
            binnacleDetailsMechanicPhoneTxt.text = "Teléfono: " + binnacle.mechanic.mechanicPhone
        }

    }

    private fun quotationListRecyclerViewSetup() {
        quotationRv.layoutManager = LinearLayoutManager(requireContext())
        quotationAdapter = QuotationAdapter(quotationList)
        quotationRv.adapter = quotationAdapter

    }

    private fun getQuotationList() {
        binnaclesReferences.child(BinnacleActivity.binnacleId).child("quotation").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showToast(requireContext(), p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (quotationList.size > 0) quotationList.clear()

                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val quotation = tmp.getValue(QuotationModel::class.java)
                        quotationList.add(quotation!!)
                        quotationAdapter.notifyDataSetChanged()
                    }
                }
            }

        })
    }

}

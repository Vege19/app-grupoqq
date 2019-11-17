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
import com.grupoqq.app.activities.MechanicActivity
import com.grupoqq.app.models.BinnacleServiceModel
import com.grupoqq.app.utils.BinnacleServiceAdapter
import com.grupoqq.app.utils.GenericAdapter
import com.grupoqq.app.utils.showToast
import kotlinx.android.synthetic.main.fragment_binnacle_services.*

class BinnacleServicesFragment : Fragment() {

    private lateinit var binnacleServiceAdapter: GenericAdapter<BinnacleServiceModel>
    private var binnacleServices = mutableListOf<BinnacleServiceModel>()
    private var binnacleServicesReference = FirebaseDatabase.getInstance().getReference("binnacles/${BinnacleActivity.binnacleId}/binnacleServices")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_binnacle_services, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binnacleServicesRecyclerViewSetup()
        getBinnacleServices()

    }

    private fun binnacleServicesRecyclerViewSetup() {
        if (BinnacleActivity.isMechanic) {
            binnacleServiceAdapter = BinnacleServiceAdapter(binnacleServices, requireContext(), isMechanic = true)
        } else {
            binnacleServiceAdapter = BinnacleServiceAdapter(binnacleServices, requireContext(), isMechanic = false)
        }
        binnacleServicesRv.layoutManager = LinearLayoutManager(requireContext())
        binnacleServicesRv.adapter = binnacleServiceAdapter
    }

    private fun getBinnacleServices() {
        binnacleServicesReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showToast(requireContext(), p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (binnacleServices.size > 0) binnacleServices.clear()

                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val binnacleService = tmp.getValue(BinnacleServiceModel::class.java)
                        binnacleServices.add(binnacleService!!)
                        binnacleServiceAdapter.notifyDataSetChanged()
                    }
                }
            }

        })
    }


}

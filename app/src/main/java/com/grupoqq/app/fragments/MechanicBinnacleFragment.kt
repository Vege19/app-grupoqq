package com.grupoqq.app.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import com.grupoqq.app.R
import com.grupoqq.app.models.BinnacleModel
import com.grupoqq.app.models.BinnacleRepairModel
import com.grupoqq.app.models.VehicleModel
import com.grupoqq.app.utils.GenericAdapter
import com.grupoqq.app.utils.getFirebaseReference
import kotlinx.android.synthetic.main.fragment_binnacle.*
import kotlinx.android.synthetic.main.fragment_mechanic_profile.*
import kotlinx.android.synthetic.main.item_repair.view.*

class MechanicBinnacleFragment : Fragment() {

    private lateinit var mBinnacle: BinnacleModel
    private var mBinnacleRepairs = mutableListOf<BinnacleRepairModel>()
    private var mAdapter = mechanicBinnacleRepairsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_binnacle, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setOnClickListeners()
        fetchArguments()
        setBinnacleVehicleData()
        setBinnacleClientData()
        setBinnacleRepairsRecyclerView()
        getBinnacleRepairs()

    }

    private fun fetchArguments() {
        mBinnacle = arguments?.getSerializable("BINNACLE_KEY") as BinnacleModel
    }

    private fun setOnClickListeners() {
        binnacleToolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setBinnacleVehicleData() {
        //gide mechanic option
        binnacleMechanicTxt.visibility = View.GONE

        getFirebaseReference("vehicles/${mBinnacle.vehicleId}").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Debug", p0.message)
            }
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val vehicle = p0.getValue(VehicleModel::class.java)
                    binnacleBrandTxt.text = getString(R.string.tag_brand) + vehicle?.vehicleBrand
                    binnacleYearTxt.text = getString(R.string.tag_year) + vehicle?.vehicleYear
                    binnacleModelTxt.text = getString(R.string.tag_model) + vehicle?.vehicleModel
                    binnacleRegistrationNumTxt.text = getString(R.string.tag_registration_number) + vehicle?.vehicleRegistrationNumber
                }
            }
        })
    }

    private fun setBinnacleClientData() {
        getFirebaseReference("clients/${mBinnacle.clientId}/clientNames").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Debug", p0.message)
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    binnacleClientTxt.text = p0.getValue(String::class.java)
                }
            }
        })
    }

    private fun getBinnacleRepairs() {
        getFirebaseReference("binnacle/${mBinnacle.binnacleId}/repairs").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Debug", p0.message)
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val repair = tmp.getValue(BinnacleRepairModel::class.java)
                        mBinnacleRepairs.add(repair!!)
                        mAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun setBinnacleRepairsRecyclerView() {
        binnacleRepairsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binnacleRepairsRecyclerView.adapter = mAdapter
    }

    private fun mechanicBinnacleRepairsAdapter(): GenericAdapter<BinnacleRepairModel> {
        return GenericAdapter(R.layout.item_repair, mBinnacleRepairs, fun (viewHolder, view, repair, _) {
            //Repair name
            setBinnacleRepairName(view.repairNameTxt, repair.repairId)
            //Status
            when (repair.binnacleRepairStatus) {
                1 -> {
                    view.repairStatusTxt.text = "Pendiente"
                    view.repairStatusTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorStatusPend))
                }
                2 -> {
                    view.repairStatusTxt.text = "En progreso"
                    view.repairStatusTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorStatusInProgress))
                }
                else -> {
                    view.repairStatusTxt.text = "Completado"
                    view.repairStatusTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorStatusCompleted))
                }
            }
            //Repair date
            if (repair.isApproved) {
                view.repairStartDate.text = repair.binnacleRepairStartDate
            } else {
                view.repairStartDate.text = "No aprobado"
            }

            viewHolder.itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("BINNACLE_KEY", mBinnacle)
                bundle.putSerializable("REPAIR_KEY", repair)
                findNavController().navigate(R.id.action_mechanicBinnacleFragment_to_mechanicRepairDetailsFragment, bundle)
            }
        })
    }

    private fun setBinnacleRepairName(view: TextView, repairId: String) {
        getFirebaseReference("repair/$repairId/repairName").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Debug", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    view.text = p0.getValue(String::class.java)
                } else {
                    Log.d("Debug", "Reference does not exist.")
                }
            }

        })
    }



}

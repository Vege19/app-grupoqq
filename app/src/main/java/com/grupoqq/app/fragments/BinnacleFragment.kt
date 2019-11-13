package com.grupoqq.app.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import com.grupoqq.app.R
import com.grupoqq.app.models.*
import com.grupoqq.app.utils.GenericAdapter
import com.grupoqq.app.utils.getFirebaseReference
import kotlinx.android.synthetic.main.fragment_binnacle.*
import kotlinx.android.synthetic.main.item_repair.*
import kotlinx.android.synthetic.main.item_repair.view.*
import kotlinx.android.synthetic.main.item_repair.view.repairNameTxt

class BinnacleFragment : Fragment() {

    private lateinit var mBinnacle: BinnacleModel
    private lateinit var mVehicle: VehicleModel
    private lateinit var mClient: ClientModel
    private lateinit var mMechanic: MechanicModel
    private var mBinnacleRepairs = mutableListOf<BinnacleRepairModel>()
    private val bundle = Bundle()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_binnacle, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fetchBinnacle()
        loadBinnacleData()
        setOnClickListeners()
        getBinnacleRepairs()

    }

    private fun fetchBinnacle() {
        mBinnacle = arguments?.getSerializable("BINNACLE_KEY") as BinnacleModel
    }

    private fun setOnClickListeners() {
        binnacleToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binnacleExpandDetailsBtn.setOnClickListener {
            //Change visibility
            binnacleVehicleDetailsContainer.let {
                it.visibility = if (it.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
            //Rotate arrow
            it.rotation = if (binnacleVehicleDetailsContainer.visibility == View.VISIBLE) 180f else 0f
        }

    }

    private fun loadRepairsRecyclerView() {
        binnancleRepairsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binnancleRepairsRecyclerView.adapter = repairsAdapter()
    }

    private fun getBinnacleRepairs() {
        getFirebaseReference("binnacle/${mBinnacle.binnacleId}/repairs").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (mBinnacleRepairs.size > 0) {
                    mBinnacleRepairs.clear()
                }

                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val repair = tmp.getValue(BinnacleRepairModel::class.java)
                        Log.d("Debug", repair.toString())
                        mBinnacleRepairs.add(repair!!)
                        loadRepairsRecyclerView()
                        repairsAdapter().notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUG", p0.message)
            }
        })
    }

    private fun repairsAdapter(): GenericAdapter<BinnacleRepairModel> {
        return GenericAdapter(R.layout.item_repair, mBinnacleRepairs, fun (viewHolder, view, repair, _) {
            if (mBinnacleRepairs.isNotEmpty()) {

                setRepairName(view.repairNameTxt, repair.repairId)
                view.repairStartDate.text = repair.binnacleRepairStartDate

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

                viewHolder.itemView.setOnClickListener {
                    bundle.putString("BINNACLE_REPAIR_ID_KEY", repair.binnacleRepairId)
                    bundle.putSerializable("REPAIR_KEY", repair)
                    bundle.putString("BINNACLE_ID_KEY", mBinnacle.binnacleId)
                    findNavController().navigate(R.id.action_binnacleFragment_to_repairDetailsFragment, bundle)
                }
            }
        })
    }

    private fun setRepairName(repairNameTxt: TextView, repairId: String) {
        getFirebaseReference("repair").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUG", p0.message)
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val repair = tmp.getValue(RepairModel::class.java)
                        if (repair?.repairId == repairId) {
                            repairNameTxt.text = repair.repairName
                            bundle.putString("REPAIR_NAME_KEY", repair.repairName)
                            repairsAdapter().notifyDataSetChanged()
                            break
                        } else {
                            Log.d("DEBUG", "Not found.")
                        }
                    }
                }
            }
        })
    }

    private fun loadBinnacleData() {
        //Load vehicle data
        getFirebaseReference("vehicles").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val vehicle = tmp.getValue(VehicleModel::class.java)
                        if (vehicle?.vehicleId == mBinnacle.vehicleId) {
                            mVehicle = vehicle
                            displayVehicleData()
                            break
                        } else {
                            Log.d("DEBUG", "Vehicle not found.")
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUG", p0.message)
            }
        })

        //Load client data
        getFirebaseReference("clients").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val client = tmp.getValue(ClientModel::class.java)
                        if (mBinnacle.clientId == client?.clientId) {
                            mClient = client
                            displayClientData()
                            break
                        } else {
                            Log.d("DEBUG", "Client not found.")
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUG", p0.message)
            }
        })

        //Load mechanic data
        getFirebaseReference("mechanics").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val mechanic = tmp.getValue(MechanicModel::class.java)
                        if (mechanic?.mechanicId == mBinnacle.mechanicId) {
                            mMechanic = mechanic
                            displayMechanicData()
                            break
                        } else {
                            Log.d("DEBUG", "Mechanic not found.")
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUG", p0.message)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun displayVehicleData() {
        binnacleBrandTxt.text = getString(R.string.tag_brand) + mVehicle.vehicleBrand
        binnacleModelTxt.text = getString(R.string.tag_model) + mVehicle.vehicleModel
        binnacleRegistrationNumTxt.text = getString(R.string.tag_registration_number) + mVehicle.vehicleRegistrationNumber
        binnacleYearTxt.text = getString(R.string.tag_year) + mVehicle.vehicleYear
    }

    @SuppressLint("SetTextI18n")
    private fun displayClientData() {
        binnacleClientTxt.text = getString(R.string.tag_client) + mClient.clientNames
    }

    private fun displayMechanicData() {
        binnacleMechanicTxt.text = getString(R.string.tag_mechanic) + mMechanic.mechanicNames
    }

}

package com.grupoqq.app.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import com.grupoqq.app.R
import com.grupoqq.app.models.BinnacleModel
import com.grupoqq.app.models.MechanicModel
import com.grupoqq.app.models.VehicleModel
import com.grupoqq.app.utils.GenericAdapter
import com.grupoqq.app.utils.getFirebaseReference
import com.grupoqq.app.utils.setGlideImage
import kotlinx.android.synthetic.main.fragment_mechanic_profile.*
import kotlinx.android.synthetic.main.item_mechanic_binnacle.view.*

class MechanicProfileFragment : Fragment() {

    private var mechanicId = "0"
    private var mMechanicBinnacles = mutableListOf<BinnacleModel>()
    private var mBinnacles = mutableListOf<BinnacleModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mechanic_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (mMechanicBinnacles.isNotEmpty()) {
            mMechanicBinnacles.clear()
        }

        getMechanicData()
        getBinnacles()

    }

    private fun getMechanicData() {
        getFirebaseReference("mechanics/$mechanicId").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("debug", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    setMechanicData(p0.getValue(MechanicModel::class.java)!!)
                }
            }
        })
    }

    private fun setMechanicData(mechanic: MechanicModel) {
        mechanicProfileImg.setGlideImage(requireContext(), mechanic.mechanicProfile, true)
        mechanicProfileNamesTxt.text = mechanic.mechanicNames
    }

    private fun getMechanicBinnacles() {
        getFirebaseReference("mechanics/$mechanicId/binnacles").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("debug", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (mMechanicBinnacles.size > 0) mMechanicBinnacles.clear()

                if (p0.exists()) {
                    for (tmp in mBinnacles) {
                        for (tmp2 in p0.children) {
                            val mechanicBinnacle = tmp2.getValue(BinnacleModel::class.java)
                            if (tmp.binnacleId == mechanicBinnacle?.binnacleId) {
                                mMechanicBinnacles.add(tmp)
                                Log.d("debug", mMechanicBinnacles.toString())
                                Log.d("debug", mBinnacles.toString())

                                setMechanicBinnaclesRecyclerView()
                                mechanicBinnaclesRecyclerViewAdapter().notifyDataSetChanged()
                            }
                        }
                    }
                } else {
                    Log.d("debug", "Reference does not exist")
                }
            }

        })
    }

    private fun getBinnacles() {
        getFirebaseReference("binnacle").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("debug", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val binnacle = tmp.getValue(BinnacleModel::class.java)
                        mBinnacles.add(binnacle!!)
                        Log.d("debug", mBinnacles.toString())
                        getMechanicBinnacles()
                    }
                } else {
                    Log.d("debug", "Reference does not exist")
                }
            }
        })
    }

    private fun setMechanicBinnaclesRecyclerView() {
        mechanicBinnaclesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mechanicBinnaclesRecyclerView.adapter = mechanicBinnaclesRecyclerViewAdapter()
    }

    private fun mechanicBinnaclesRecyclerViewAdapter(): GenericAdapter<BinnacleModel> {
        return GenericAdapter(R.layout.item_mechanic_binnacle, mMechanicBinnacles, fun (vh, v, b, _) {
            setBinnacleClient(v.mechanicBinnacleClientTxt, b.clientId)
            v.mechanicBinnacleCodeTxt.text = b.binnacleId
            setBinnacleVehicle(v.mechanicBinnacleVehicleTxt, b.vehicleId)

            vh.itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("BINNACLE_KEY", b)
                findNavController().navigate(R.id.action_mechanicProfileFragment_to_mechanicBinnacleFragment, bundle)
            }

        })
    }

    private fun setBinnacleClient(view: TextView, clientId: String) {
        getFirebaseReference("clients/$clientId/clientNames").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Debug", p0.message)
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    view.text = "Cliente: " + p0.getValue(String::class.java)
                }
            }
        })
    }

    private fun setBinnacleVehicle(view: TextView, vehicleId: String) {
        getFirebaseReference("vehicles/$vehicleId").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Debug", p0.message)
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val vehicle = p0.getValue(VehicleModel::class.java)
                    view.text = "Vehículo: ${vehicle?.vehicleBrand} ${vehicle?.vehicleModel} ${vehicle?.vehicleYear}"
                }
            }
        })

    }



}

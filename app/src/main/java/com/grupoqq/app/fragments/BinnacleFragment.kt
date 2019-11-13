package com.grupoqq.app.fragments

import android.annotation.SuppressLint
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import com.grupoqq.app.R
import com.grupoqq.app.models.BinnacleModel
import com.grupoqq.app.models.ClientModel
import com.grupoqq.app.models.MechanicModel
import com.grupoqq.app.models.VehicleModel
import com.grupoqq.app.utils.getFirebaseReference
import kotlinx.android.synthetic.main.fragment_binnacle.*

class BinnacleFragment : Fragment() {

    private lateinit var mBinnacle: BinnacleModel
    private lateinit var mVehicle: VehicleModel
    private lateinit var mClient: ClientModel
    private lateinit var mMechanic: MechanicModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_binnacle, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setOnClickListeners()
        fetchBinnacle()
        loadBinnacleData()

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

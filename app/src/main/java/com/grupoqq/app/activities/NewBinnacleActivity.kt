package com.grupoqq.app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.grupoqq.app.R
import com.grupoqq.app.models.*
import com.grupoqq.app.utils.GenericAdapter
import com.grupoqq.app.utils.MechanicAdapter
import com.grupoqq.app.utils.ServiceAdapter
import com.grupoqq.app.utils.showToast
import kotlinx.android.synthetic.main.activity_new_binnacle.*
import kotlinx.android.synthetic.main.sheet_mechanics.view.*

class NewBinnacleActivity : AppCompatActivity() {

    private var services = mutableListOf<ServiceModel>()
    private var selectedServices = mutableListOf<ServiceModel>()
    private lateinit var serviceAdapter: GenericAdapter<ServiceModel>

    private var mechanics = mutableListOf<MechanicModel>()
    private lateinit var mechanicAdapter: GenericAdapter<MechanicModel>

    //Models to be uploaded
    private var newVehicle = VehicleModel()
    private var newBinnacle = BinnacleModel()
    private var newBinnacleService = BinnacleServiceModel()
    private var newClient = ClientModel()

    companion object {
        var isMechanicSelected = false
        var newMechanic = MechanicModel()
    }

    //REFERENCES
    private val servicesReference = FirebaseDatabase.getInstance().getReference("services")
    private val mechanicsReference = FirebaseDatabase.getInstance().getReference("mechanics")
    private val binnaclesReference = FirebaseDatabase.getInstance().getReference("binnacles")
    private val vehiclesReference = FirebaseDatabase.getInstance().getReference("vehicles")
    private val clientsReference = FirebaseDatabase.getInstance().getReference("clients")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_binnacle)

        setOnClickListeners()
        serviceRecyclerViewSetup()
        getServices()

    }

    private fun setOnClickListeners() {
        selectMechanicBtn.setOnClickListener {
            showMechanicsBottomSheet()
        }

        addNewBinnacleButton.setOnClickListener {
            Log.d("Debug", selectedServices.toString())

            //Client Validations
            val clientNames = newBinnacleInputNameTxt.text.toString().trim()
            val clientEmail = newBinnacleInputEmailTxt.text.toString().trim()
            val clientPhone = newBinnacleInputPhoneTxt.text.toString().trim()

            if (clientNames.isEmpty()) {
                newBinnacleNameTil.error = "Campo requerido"

            } else {
                if (clientEmail.isEmpty()) {
                    newBinnacleEmailTil.error = "Campo requerido"

                } else {
                    if (clientPhone.isEmpty()) {
                        newBinnaclePhoneTil.error = "Campo requerido"

                    } else {
                        //Vehicle validations
                       /* val vehicleBrand = brandSpinner.selectedItem.toString()
                        val vehicleModel = modelSpinner.selectedItem.toString()
                        val vehicleYear = modelSpinner.selectedItem.toString()*/
                        val vehicleRegistrationNumber = newBinnacleInputRegNumTxt.text.toString().trim()

                        if (vehicleRegistrationNumber.isEmpty()) {
                            newBinnacleRegNumTil.error = "Campo requerido"

                        } else {
                            //Services validations
                            if (selectedServices.size <= 0) {
                                showToast(baseContext, "Seleccione al menos un servicio.")

                            } else {
                                //Mechanic validation
                                if (isMechanicSelected) {
                                    //Do it
                                    showToast(baseContext, "Ok")
                                    //Generate keys
                                    val binnacleId = binnaclesReference.push().key
                                    val vehicleId = vehiclesReference.push().key
                                    val clientId = clientsReference.push().key
                                    //Load vehicle
                                    newVehicle = VehicleModel(vehicleId!!, "Toyota", "Corolla", "2019")
                                    vehiclesReference.child(vehicleId).setValue(newVehicle)
                                    //Load client
                                    newClient = ClientModel(clientId!!, clientNames, clientEmail, clientPhone)
                                    clientsReference.child(clientId).setValue(newClient)
                                    //Set vehicle to client and reverse
                                    vehiclesReference.child(vehicleId).child("client").setValue(newClient)
                                    clientsReference.child(clientId).child("vehicle").setValue(newVehicle)
                                    //Load binnacle
                                    newBinnacle = BinnacleModel(binnacleId!!, newClient, newVehicle, newMechanic)
                                    binnaclesReference.child(binnacleId).setValue(newBinnacle)
                                    //Load binnacle services
                                    for (tmp in selectedServices) {
                                        val binnacleServiceId = binnaclesReference.child(binnacleId).child("binnacleServices").push().key
                                        newBinnacleService = BinnacleServiceModel(binnacleServiceId!!, 1, "", "", tmp)
                                        binnaclesReference.child(binnacleId).child("binnacleServices").child(binnacleServiceId).setValue(newBinnacleService)
                                    }
                                    finish()
                                } else {
                                    showToast(baseContext, "Seleccione un mecánico.")

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun serviceRecyclerViewSetup() {
        serviceAdapter = ServiceAdapter(services, selectedServices, this)
        newBinnacleServicesRv.layoutManager = LinearLayoutManager(this)
        newBinnacleServicesRv.adapter = serviceAdapter
    }

    private fun getServices() {
        servicesReference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showToast(baseContext, p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (services.size > 0) services.clear()
                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val service = tmp.getValue(ServiceModel::class.java)
                        services.add(service!!)
                        serviceAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun getMechanics() {
        mechanicsReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showToast(baseContext, p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (mechanics.size > 0) mechanics.clear()

                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val mechanic = tmp.getValue(MechanicModel::class.java)
                        mechanics.add(mechanic!!)
                        mechanicAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun showMechanicsBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.sheet_mechanics, null)
        bottomSheetDialog.setContentView(view)
        mechanicRecyclerViewSetup(view, bottomSheetDialog, newMechanic)
        bottomSheetDialog.show()
        getMechanics()
    }

    private fun mechanicRecyclerViewSetup(view: View, bottomSheetDialog: BottomSheetDialog, mechanic: MechanicModel) {
        view.sheetNewBinnacleMechanicsRv.layoutManager = LinearLayoutManager(this)
        mechanicAdapter = MechanicAdapter(mechanics, this, newBinnacleMechanicImg, newBinnacleMechanicNameTxt, bottomSheetDialog)
        view.sheetNewBinnacleMechanicsRv.adapter = mechanicAdapter
    }

}

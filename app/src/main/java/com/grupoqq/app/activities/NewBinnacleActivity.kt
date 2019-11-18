package com.grupoqq.app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import kotlinx.android.synthetic.main.layout_actionbar.view.*
import kotlinx.android.synthetic.main.sheet_mechanics.view.*
import java.lang.reflect.Array

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

    private var quotation = mutableListOf<QuotationModel>()

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
        newBinnacleToolbar.toolbar.title = "Nueva bitácora"
        newBinnacleToolbar.toolbar.setNavigationIcon(R.drawable.ic_back)

        setOnClickListeners()
        spinnersSetUp()
        serviceRecyclerViewSetup()
        getServices()

    }

    private fun setOnClickListeners() {
        selectMechanicBtn.setOnClickListener {
            showMechanicsBottomSheet()
        }

        addNewBinnacleButton.setOnClickListener {
            uploadBinnacle()
        }

        newBinnacleToolbar.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun spinnersSetUp() {
        //Year array
        val years = arrayListOf<CharSequence>()
        for (i in 2000..2019) {
            years.add(i.toString())
        }

        //Defining adapters
        val brandsAdapter = ArrayAdapter.createFromResource(this, R.array.brands, android.R.layout.simple_spinner_dropdown_item)
        val hondaAdapter = ArrayAdapter.createFromResource(this, R.array.honda_models, android.R.layout.simple_spinner_dropdown_item)
        val hyundaiAdapter = ArrayAdapter.createFromResource(this, R.array.hyundai_models, android.R.layout.simple_spinner_dropdown_item)
        val nissanAdapter = ArrayAdapter.createFromResource(this, R.array.nissan_models, android.R.layout.simple_spinner_dropdown_item)
        val mitsubishiAdapter = ArrayAdapter.createFromResource(this, R.array.mitsubishi_models, android.R.layout.simple_spinner_dropdown_item)
        val toyotaAdapter = ArrayAdapter.createFromResource(this, R.array.toyota_models, android.R.layout.simple_spinner_dropdown_item)
        val yearAdapter = ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, years)

        //Setting adapter to spinners
        brandSpinner.adapter = brandsAdapter
        brandSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> modelSpinner.adapter = toyotaAdapter
                    1 -> modelSpinner.adapter = hondaAdapter
                    2 -> modelSpinner.adapter = hyundaiAdapter
                    3 -> modelSpinner.adapter = nissanAdapter
                    else -> modelSpinner.adapter = mitsubishiAdapter
                }
            }
        }

        yearSpinner.adapter = yearAdapter

    }

    private fun uploadBinnacle() {
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
                     val vehicleBrand = brandSpinner.selectedItem.toString()
                     val vehicleModel = modelSpinner.selectedItem.toString()
                     val vehicleYear = yearSpinner.selectedItem.toString()
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
                                val binnacleId = "BNC${(1..9999).random()}"
                                val vehicleId = vehiclesReference.push().key
                                val clientId = clientsReference.push().key
                                //Load vehicle
                                newVehicle = VehicleModel(
                                    vehicleId!!,
                                    vehicleBrand,
                                    vehicleModel,
                                    vehicleYear,
                                    vehicleRegistrationNumber
                                )
                                vehiclesReference.child(vehicleId).setValue(newVehicle)
                                //Load client
                                newClient =
                                    ClientModel(clientId!!, clientNames, clientEmail, clientPhone)
                                clientsReference.child(clientId).setValue(newClient)
                                //Set vehicle to client and reverse
                                vehiclesReference.child(vehicleId).child("client")
                                    .setValue(newClient)
                                clientsReference.child(clientId).child("vehicle")
                                    .setValue(newVehicle)
                                //Load binnacle
                                newBinnacle =
                                    BinnacleModel(binnacleId, newClient, newVehicle, newMechanic)
                                binnaclesReference.child(binnacleId).setValue(newBinnacle)
                                //Put binnacle to mechanic
                                val mechanicBinnacleId = mechanicsReference.child(newMechanic.mechanicId).child("binnacles").push().key
                                mechanicsReference.child(newMechanic.mechanicId).child("mechanicBinnacles").child(mechanicBinnacleId!!).setValue(newBinnacle)
                                //Load binnacle services
                                for (tmp in selectedServices) {
                                    //Binnacle services
                                    val binnacleServiceId = binnaclesReference.child(binnacleId)
                                        .child("binnacleServices").push().key
                                    newBinnacleService =
                                        BinnacleServiceModel(binnacleServiceId!!, 1, "", "", tmp)
                                    binnaclesReference.child(binnacleId).child("binnacleServices")
                                        .child(binnacleServiceId).setValue(newBinnacleService)
                                }
                                //Upload quotation
                                binnaclesReference.child(binnacleId).child("quotation").setValue(quotation)
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

    private fun serviceRecyclerViewSetup() {
        serviceAdapter = ServiceAdapter(services, selectedServices, this)
        newBinnacleServicesRv.layoutManager = LinearLayoutManager(this)
        newBinnacleServicesRv.adapter = serviceAdapter
    }

    private fun getServices() {
        servicesReference.addValueEventListener(object : ValueEventListener {
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

    private fun mechanicRecyclerViewSetup(
        view: View,
        bottomSheetDialog: BottomSheetDialog,
        mechanic: MechanicModel
    ) {
        view.sheetNewBinnacleMechanicsRv.layoutManager = LinearLayoutManager(this)
        mechanicAdapter = MechanicAdapter(
            mechanics,
            this,
            newBinnacleMechanicImg,
            newBinnacleMechanicNameTxt,
            bottomSheetDialog
        )
        view.sheetNewBinnacleMechanicsRv.adapter = mechanicAdapter
    }

}

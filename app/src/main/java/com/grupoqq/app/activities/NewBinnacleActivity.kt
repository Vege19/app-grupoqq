package com.grupoqq.app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.grupoqq.app.R
import com.grupoqq.app.models.MechanicModel
import com.grupoqq.app.models.ServiceModel
import com.grupoqq.app.utils.GenericAdapter
import com.grupoqq.app.utils.MechanicAdapter
import com.grupoqq.app.utils.ServiceAdapter
import com.grupoqq.app.utils.showToast
import kotlinx.android.synthetic.main.activity_new_binnacle.*
import kotlinx.android.synthetic.main.sheet_mechanics.view.*

class NewBinnacleActivity : AppCompatActivity() {

    private var services = mutableListOf<ServiceModel>()
    private lateinit var serviceAdapter: GenericAdapter<ServiceModel>

    private var mechanics = mutableListOf<MechanicModel>()
    private lateinit var mechanicAdapter: GenericAdapter<MechanicModel>

    //REFERENCES
    private val servicesReference = FirebaseDatabase.getInstance().getReference("services")
    private val mechanicsReference = FirebaseDatabase.getInstance().getReference("mechanics")

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
    }

    private fun serviceRecyclerViewSetup() {
        serviceAdapter = ServiceAdapter(services, this)
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
        mechanicRecyclerViewSetup(view, bottomSheetDialog)
        bottomSheetDialog.show()
        getMechanics()
    }

    private fun mechanicRecyclerViewSetup(view: View, bottomSheetDialog: BottomSheetDialog) {
        view.sheetNewBinnacleMechanicsRv.layoutManager = LinearLayoutManager(this)
        mechanicAdapter = MechanicAdapter(mechanics, this, newBinnacleMechanicImg, newBinnacleMechanicNameTxt, bottomSheetDialog)
        view.sheetNewBinnacleMechanicsRv.adapter = mechanicAdapter
    }

}

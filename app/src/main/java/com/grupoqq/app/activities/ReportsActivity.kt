package com.grupoqq.app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.grupoqq.app.R
import com.grupoqq.app.models.BinnacleServiceModel
import com.grupoqq.app.models.SparePartModel
import com.grupoqq.app.utils.*
import kotlinx.android.synthetic.main.activity_reports.*
import kotlinx.android.synthetic.main.layout_actionbar.view.*
import kotlinx.android.synthetic.main.layout_service_spare_parts.*
import kotlinx.android.synthetic.main.layout_service_spare_parts.view.*

class ReportsActivity : AppCompatActivity() {

    private lateinit var binnacleServicesReference: DatabaseReference
    private var selectedSpareParts = arrayListOf<SparePartModel>()
    private var spareParts = mutableListOf<SparePartModel>()
    private lateinit var sparePartAdapter: GenericAdapter<SparePartModel>
    private val sparePartsReference = FirebaseDatabase.getInstance().getReference("spareParts")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        getReference()
        toolbarSetup()
        getBinnacleService()

    }

    private fun getReference() {
        val binnacleServiceId = intent.getStringExtra("BINNACLE_SERVICE_KEY")
        binnacleServicesReference = FirebaseDatabase.getInstance()
            .getReference("binnacles/${BinnacleActivity.binnacleId}/binnacleServices/$binnacleServiceId")
    }

    private fun toolbarSetup() {
        reportsToolbar.toolbar.title = "Reportes"
        reportsToolbar.toolbar.setNavigationIcon(R.drawable.ic_back)
        reportsToolbar.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun getBinnacleService() {
        binnacleServicesReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showToast(baseContext, p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val binnacleService = p0.getValue(BinnacleServiceModel::class.java)
                    verifyStatus(binnacleService?.binnacleServiceStatus!!)
                }
            }

        })
    }

    private fun verifyStatus(status: Int) {
        //If mechanic has to send to client which spare parts he need to use
        if (status == 1 && BinnacleActivity.isMechanic) {
            sparePartServiceLayout.makeVisible()
            addNewReportFab.makeGone()
            sparePartServiceLayout.serviceSparePartsBtn.text = "ENVIAR"
            sparePartServiceLayout.textView.text = "Selecciona los productos y repuestos necesarios para realizar este servicio."
            sparePartsRecyclerViewSetup(spareParts, true)
            getSpareParts()
            sparePartServiceLayout.serviceSparePartsBtn.setOnClickListener {
                if (selectedSpareParts.size <= 0) {
                    showToast(this, "Seleccione los repuestos requeridos.")
                } else {
                    //Load selected spare parts
                    for (item in selectedSpareParts) {
                        val id =
                            binnacleServicesReference.child("binnacleServiceSpareParts").push().key
                        binnacleServicesReference.child("binnacleServiceSpareParts").child(id!!)
                            .setValue(item)
                    }
                    //Switch status in wait
                    binnacleServicesReference.child("binnacleServiceStatus").setValue(2)
                }
            }

        }
        //If mechanic finished and client is in wait to approve
        else if (status == 2 && BinnacleActivity.isMechanic) {
            sparePartServiceLayout.makeGone()
            reportsMessageText.makeVisible()
            reportsMessageText.text = "Este servicio se encuentra en espera de aprobación por parte del cliente."
        }
        //If the mechanic already sent spare parts
        else if (status == 2 && !BinnacleActivity.isMechanic) {
            sparePartServiceLayout.makeVisible()
            addNewReportFab.makeGone()
            sparePartServiceLayout.serviceSparePartsBtn.text = "APROBAR"
            sparePartsRecyclerViewSetup(selectedSpareParts, isMechanic = false)
            getBinnacleServiceSpareParts()
            sparePartServiceLayout.serviceSparePartsBtn.setOnClickListener {
                binnacleServicesReference.child("binnacleServiceStatus").setValue(3)
            }
        }
        //If client approved, mechanic can add reports
        else if (status == 3 && BinnacleActivity.isMechanic) {
            sparePartServiceLayout.makeGone()
            reportsMessageText.makeGone()
            reportsRv.makeVisible()
            addNewReportFab.makeVisible()
        }
        //If client approved service, he can see reports
        else if (status == 3 && !BinnacleActivity.isMechanic) {
            sparePartServiceLayout.makeGone()
            reportsMessageText.makeGone()
            addNewReportFab.makeGone()
            reportsRv.makeVisible()
        }
    }

    private fun getSpareParts() {
        sparePartsReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showToast(baseContext, p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (spareParts.size > 0) spareParts.clear()

                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val sparePart = tmp.getValue(SparePartModel::class.java)
                        spareParts.add(sparePart!!)
                        sparePartAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun getBinnacleServiceSpareParts() {
        binnacleServicesReference.child("binnacleServiceSpareParts")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    showToast(baseContext, p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (selectedSpareParts.size > 0) selectedSpareParts.clear()

                    if (p0.exists()) {
                        for (tmp in p0.children) {
                            val sparePart = tmp.getValue(SparePartModel::class.java)
                            selectedSpareParts.add(sparePart!!)
                            sparePartAdapter.notifyDataSetChanged()
                        }
                    }
                }
            })
    }

    private fun sparePartsRecyclerViewSetup(list: List<SparePartModel>, isMechanic: Boolean) {
        sparePartAdapter = SparePartAdapter(list, baseContext, isMechanic, selectedSpareParts)
        serviceSparePartsRv.layoutManager = LinearLayoutManager(this)
        serviceSparePartsRv.adapter = sparePartAdapter
    }

}

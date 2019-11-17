package com.grupoqq.app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.*
import com.grupoqq.app.R
import com.grupoqq.app.utils.showToast
import kotlinx.android.synthetic.main.activity_reports.*
import kotlinx.android.synthetic.main.layout_actionbar.view.*

class ReportsActivity : AppCompatActivity() {

    private lateinit var binnacleServicesReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        getReference()
        toolbarSetup()
        getBinnacleService()

    }

    private fun getReference() {
        val binnacleServiceId = intent.getStringExtra("BINNACLE_SERVICE_KEY")
        binnacleServicesReference = FirebaseDatabase.getInstance().getReference("binnacles/${BinnacleActivity.binnacleId}/binnacleServices/$binnacleServiceId")
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

                }
            }

        })
    }

    private fun verifyStatus(status: Int) {
        if (status == 2) {

        }
    }

}

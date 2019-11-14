package com.grupoqq.app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.grupoqq.app.R
import com.grupoqq.app.models.BinnacleModel
import com.grupoqq.app.models.ClientModel
import com.grupoqq.app.models.ServiceModel
import com.grupoqq.app.models.VehicleModel
import com.grupoqq.app.utils.getFirebaseReference
import kotlinx.android.synthetic.main.activity_new_binnacle.*
import java.util.*

class NewBinnacleActivity : AppCompatActivity() {

    private var mBinnacle = BinnacleModel()
    private var mVehicle = VehicleModel()
    private var mClient = ClientModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_binnacle)

        startSpinners()

    }

    private fun startSpinners() {
        var brand = ""
        var model = ""
        var year = ""

        var brands = listOf(resources.getStringArray(R.array.marca_autos))

        newBinnacleBrandSpinner.setItems(brands)
        newBinnacleBrandSpinner.setOnItemSelectedListener { view, position, id, item ->
            Log.d("Debug", item.toString())
        }

    }
}

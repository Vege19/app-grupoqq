package com.grupoqq.app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import com.grupoqq.app.R
import com.grupoqq.app.models.MechanicModel
import com.grupoqq.app.models.ServiceModel
import com.grupoqq.app.models.SparePartModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setOnClickListeners()
        
    }

    private fun setOnClickListeners() {
        //Intent to admin activity
        cardOptionOne.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
        }

        cardOptionTwo.setOnClickListener {
           intentToCodeInput(false)
        }

        cardOptionThree.setOnClickListener {
            intentToCodeInput(true)
        }
    }

    private fun intentToCodeInput(isMechanic: Boolean) {
        val intent = Intent(this, InputCodeActivity::class.java)
        intent.putExtra("IS_MECHANIC", isMechanic)
        startActivity(intent)
    }

}

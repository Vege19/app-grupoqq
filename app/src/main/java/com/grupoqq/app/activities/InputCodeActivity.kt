package com.grupoqq.app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.grupoqq.app.R
import com.grupoqq.app.models.BinnacleModel
import com.grupoqq.app.models.MechanicModel
import com.grupoqq.app.utils.showToast
import kotlinx.android.synthetic.main.activity_input_code.*
import kotlinx.android.synthetic.main.layout_actionbar.view.*

class InputCodeActivity : AppCompatActivity() {

    private var isMechanic = false
    private val binnaclesReference = FirebaseDatabase.getInstance().getReference("binnacles")
    private val mechanicsReference = FirebaseDatabase.getInstance().getReference("mechanics")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_code)

        toolbarSetup()
        setInstructions()
        setOnClickListeners()

    }

    private fun setInstructions() {
        isMechanic = intent.getBooleanExtra("IS_MECHANIC", false)
        if (isMechanic) {
            inputCodeToolbar.toolbar.title = "Ingrese como mecánico"
            inputCodeInstructionTxt.text =
                "Ingrese su código de mecánico para ingresar a su perfil."
            inputCodeBtn.text = "INGRESAR"
        }
    }

    private fun toolbarSetup() {
        inputCodeToolbar.toolbar.title = "Consultar bitácora"
        inputCodeToolbar.toolbar.setNavigationIcon(R.drawable.ic_back)
        inputCodeToolbar.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setOnClickListeners() {
        inputCodeBtn.setOnClickListener {
            val code = inputCodeInputTxt.text.toString().trim()
            if (isMechanic) {
                findMechanic(code)
            } else {
                findBinnacle(code)
            }
        }

    }


    private fun findBinnacle(code: String) {
        binnaclesReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showToast(baseContext, p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val binnacle = tmp.getValue(BinnacleModel::class.java)
                        if (binnacle?.binnacleId == code) {
                            intentToBinnacleActivity(binnacle.binnacleId)
                            break
                        } else {
                            showToast(baseContext, "Not found.")
                        }
                    }
                }
            }
        })
    }

    private fun findMechanic(code: String) {
        mechanicsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showToast(baseContext, p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val mechanic = tmp.getValue(MechanicModel::class.java)
                        if (mechanic?.mechanicId == code) {
                            intentToMechanicActivity(code)
                            break
                        }
                    }
                }
            }

        })
    }

    private fun intentToBinnacleActivity(code: String) {
        val intent = Intent(this, BinnacleActivity::class.java)
        intent.putExtra("BINNACLE_KEY", code)
        startActivity(intent)
    }

    private fun intentToMechanicActivity(code: String) {
        val intent = Intent(this, MechanicActivity::class.java)
        intent.putExtra("MECHANIC_KEY", code)
        startActivity(intent)
    }

}

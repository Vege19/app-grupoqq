package com.grupoqq.app.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.grupoqq.app.R
import com.grupoqq.app.models.BinnacleServiceModel
import com.grupoqq.app.models.ReportModel
import com.grupoqq.app.models.SparePartModel
import com.grupoqq.app.utils.*
import kotlinx.android.synthetic.main.activity_reports.*
import kotlinx.android.synthetic.main.layout_actionbar.view.*
import kotlinx.android.synthetic.main.layout_service_spare_parts.*
import kotlinx.android.synthetic.main.layout_service_spare_parts.view.*
import kotlinx.android.synthetic.main.layout_service_spare_parts.view.textView
import kotlinx.android.synthetic.main.sheet_new_report.view.*
import java.lang.Exception

class ReportsActivity : AppCompatActivity() {

    private lateinit var binnacleServicesReference: DatabaseReference
    private var selectedSpareParts = arrayListOf<SparePartModel>()
    private var spareParts = mutableListOf<SparePartModel>()
    private lateinit var sparePartAdapter: GenericAdapter<SparePartModel>
    private val sparePartsReference = FirebaseDatabase.getInstance().getReference("spareParts")
    private var photoUri: Uri? = null
    private var binnacleServiceId = ""
    private val storageReference = FirebaseStorage.getInstance().reference
    private val mReport = ReportModel()

    private lateinit var bottomSheet: BottomSheetDialog
    private lateinit var bottomSheetView: View

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)


        getReference()
        toolbarSetup()
        getBinnacleService()

    }

    private fun getReference() {
        binnacleServiceId = intent.getStringExtra("BINNACLE_SERVICE_KEY")
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
            sparePartServiceLayout.textView.text =
                "Selecciona los productos y repuestos necesarios para realizar este servicio."
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
            reportsMessageText.text =
                "Este servicio se encuentra en espera de aprobación por parte del cliente."
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
            addNewReportFab.setOnClickListener {
                showBottomSheet()
            }
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

    private fun showBottomSheet() {
        bottomSheet = BottomSheetDialog(this)
        bottomSheetView = LayoutInflater.from(this).inflate(R.layout.sheet_new_report, null, false)
        bottomSheet.setContentView(bottomSheetView)
        bottomSheet.show()
        //Validation
        bottomSheetView.reportPhotoImg.setOnClickListener {
            checkPermissions()
        }

        bottomSheetView.sendReportBtn.setOnClickListener {
            val description = bottomSheetView.reportDescriptionInputTxt.text.toString().trim()
            if (description.isEmpty()) {
                bottomSheetView.reportDescriptionInputLyt.error = "Campo obligatorio"
            } else {
                if (photoUri == null) {
                    showToast(baseContext, "Adjunte una foto")
                } else {
                    showToast(baseContext, "ok")
                    mReport.reportDescription = description
                    uploadPhoto()
                    bottomSheet.dismiss()
                }
            }
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                2
            )
        } else {
            intentToCamera()
        }
    }

    private fun intentToCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Camera")
        photoUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            bottomSheetView.reportPhotoImg.setImageURI(photoUri)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 2) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intentToCamera()
            } else {
                showToast(this, "No se obtuvieron permisos de cámara")
            }
        }
    }

    private fun uploadPhoto() {
        storageReference.child("photos").child(photoUri?.lastPathSegment!!).putFile(photoUri!!)
            .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                    showToast(baseContext, "Succeded")
                    val id = binnacleServicesReference.child("reports").push().key
                    val url = p0?.storage?.downloadUrl.toString()
                    mReport.reportId = id!!
                    mReport.reportPhoto = url
                    mReport.reportDateTime = getDateTime()
                    binnacleServicesReference.child("reports").child(id).setValue(mReport)
                    bottomSheet.dismiss()
                }
            })
            .addOnFailureListener(object: OnFailureListener {
                override fun onFailure(p0: Exception) {
                    p0.printStackTrace()
                }

            })
    }

}

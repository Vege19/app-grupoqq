package com.grupoqq.app.fragments


import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.grupoqq.app.R
import com.grupoqq.app.models.BinnacleModel
import com.grupoqq.app.models.BinnacleRepairModel
import com.grupoqq.app.models.ReportModel
import com.grupoqq.app.utils.*
import kotlinx.android.synthetic.main.fragment_repair_details.*
import kotlinx.android.synthetic.main.item_report.view.*
import kotlinx.android.synthetic.main.sheet_add_report.*
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.time.LocalDateTime
import java.util.jar.Manifest

class MechanicRepairDetailsFragment : Fragment() {

    private lateinit var mBinnacle: BinnacleModel
    private lateinit var mRepair: BinnacleRepairModel
    private var mReports = mutableListOf<ReportModel>()
    private var mReportAdapter = reportsAdapter()
    private lateinit var mBottomSheetDialog: BottomSheetDialog
    private var photoUri: Uri? = null
    private var mReport: ReportModel? = null
    private var reportsReference: DatabaseReference? = null
    private var newReportId = ""

    private val CAMERA_REQUEST_CODE = 1
    private val PERMISSION_CAMERA_REQUEST_CODE = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repair_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addNewReportFab.visibility = View.VISIBLE
        repairDetailInstructionTxt.text =
            "¡Reporta al cliente tu trabajo! Cada vez que termines un proceso, informale al cliente agregando un nuevo reporte."
        setOnClickListeners()
        fetchArguments()
        setBinnacleRepairData()
        setReportsRecyclerView()
        getReports()

    }

    private fun fetchArguments() {
        mBinnacle = arguments?.getSerializable("BINNACLE_KEY") as BinnacleModel
        mRepair = arguments?.getSerializable("REPAIR_KEY") as BinnacleRepairModel
    }

    private fun setOnClickListeners() {
        repairDetailsToolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        addNewReportFab.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun setBinnacleRepairData() {
        //Repair name
        getFirebaseReference("repair/${mRepair.repairId}/repairName").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Debug", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    repairDetailNameTxt.text = p0.getValue(String::class.java)
                } else {
                    Log.d("Debug", "Reference does not exist.")
                }
            }
        })

        //Repair date and status
        getFirebaseReference("binnacle/${mBinnacle.binnacleId}/repairs/${mRepair.binnacleRepairId}").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        val repair = p0.getValue(BinnacleRepairModel::class.java)
                        if (repair!!.isApproved!!) {
                            repairDetailStartDateTxt.text = repair?.binnacleRepairStartDate
                        } else {
                            repairDetailStartDateTxt.text = "Sin aprobar"
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("Debug", p0.message)
                }
            })
    }

    private fun getReports() {
        getFirebaseReference("binnacle/${mBinnacle.binnacleId}/repairs/${mRepair.binnacleRepairId}/reports").addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("Debug", p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (mReports.size > 0) mReports.clear()

                    if (p0.exists()) {
                        for (tmp in p0.children) {
                            val repair = tmp.getValue(ReportModel::class.java)
                            mReports.add(repair!!)
                            mReportAdapter.notifyDataSetChanged()
                        }
                    }
                }

            })
    }

    private fun setReportsRecyclerView() {
        reportsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mReportAdapter = reportsAdapter()
        reportsRecyclerView.adapter = mReportAdapter
    }

    private fun reportsAdapter(): GenericAdapter<ReportModel> {
        return GenericAdapter(R.layout.item_report, mReports, fun(viewHolder, view, report, _) {
            view.reportPictureImg.setGlideImage(requireContext(), report.reportPicture)
            view.reportDescriptionTxt.text = report.reportDescription
            view.reportDateTimeTxt.text = report.reportDateTime
        })
    }

    private fun showBottomSheet() {
        mBottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetView =
            LayoutInflater.from(requireContext()).inflate(R.layout.sheet_add_report, null)
        mBottomSheetDialog.setContentView(bottomSheetView)
        //Show bs
        mBottomSheetDialog.show()
        //Events
        mBottomSheetDialog.closeBottomSheetBtn.setOnClickListener { mBottomSheetDialog.dismiss() }
        mBottomSheetDialog.addReportPhotoBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                )
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    PERMISSION_CAMERA_REQUEST_CODE
                )
            } else {
                getPhoto()
            }
        }

        mBottomSheetDialog.reportBtn.setOnClickListener {
            val description = mBottomSheetDialog.reportDescriptionInputTxt.text.toString().trim()
            if (description.isEmpty()) {
                mBottomSheetDialog.reportDescriptionInputLayout.error = "Campo obligatorio"
            } else {
                if (photoUri != null) {
                    reportsReference = getFirebaseReference("binnacle/${mBinnacle.binnacleId}/repairs/${mRepair.binnacleRepairId}/reports")
                    newReportId = reportsReference?.push()?.key!!
                    mReport = ReportModel()
                    mReport?.reportId = newReportId
                    mReport?.reportDescription = description
                    mReport?.reportDateTime = getDateTime()
                    showToast(mReport.toString(), requireContext())
                    //upload report
                    reportsReference!!.child(newReportId).setValue(mReport)
                    mBottomSheetDialog.dismiss()
                    uploadPhoto()
                } else {
                    showToast("No photo attached", requireContext())
                }
            }
        }
    }

    private fun getPhoto() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Camera")
        photoUri =
            activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mBottomSheetDialog.addReportPhotoBtn.setImageURI(photoUri)
            //showToast(photoUri.toString(), requireContext())
        } else {
            showToast("Error", requireContext())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            } else {
                showToast("Necesitas permiso de camara.", requireContext())
            }
        }
    }

    private fun uploadPhoto() {
        val file = Uri.fromFile(File(photoUri.toString()))
        val storageReference = FirebaseStorage.getInstance().getReference()
        val imageRef = storageReference.child(photoUri.toString())

            imageRef.putFile(file).addOnSuccessListener(object: OnSuccessListener<UploadTask.TaskSnapshot> {
            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                imageRef.downloadUrl.addOnSuccessListener(object: OnSuccessListener<Uri> {
                    override fun onSuccess(p0: Uri?) {
                        val imageReference = reportsReference?.child(newReportId)
                        var hashMap = HashMap<String, String>()
                        hashMap.put("reportPicture", p0.toString())

                        imageReference?.setValue(hashMap)?.addOnSuccessListener(object : OnSuccessListener<Void> {
                            override fun onSuccess(p0: Void?) {
                                showToast("Success", requireContext())
                            }
                        })
                    }
                })
                    .addOnFailureListener(object: OnFailureListener {
                        override fun onFailure(p0: Exception) {
                            showToast(p0.message!!, requireContext())
                        }

                    })
            }
        })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(p0: Exception) {
                    showToast(p0.message!!, requireContext())
                }
            })
    }


}

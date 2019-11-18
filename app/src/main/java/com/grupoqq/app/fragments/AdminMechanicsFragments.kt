package com.grupoqq.app.fragments


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

import com.grupoqq.app.R
import com.grupoqq.app.models.MechanicModel
import com.grupoqq.app.utils.*
import kotlinx.android.synthetic.main.fragment_admin_mechanics_fragments.*
import kotlinx.android.synthetic.main.sheet_add_mechanic.*
import kotlinx.android.synthetic.main.sheet_add_mechanic.view.*
import java.lang.Exception

class AdminMechanicsFragments : Fragment() {

    private var mechanics = mutableListOf<MechanicModel>()
    private lateinit var mechanicAdapter: GenericAdapter<MechanicModel>
    private var mechanicsReference = FirebaseDatabase.getInstance().getReference("mechanics")
    private lateinit var bottomSheet: BottomSheetDialog
    private lateinit var sheetView: View
    private lateinit var uri: Uri
    private val storageReference = FirebaseStorage.getInstance().reference
    private lateinit var progressDialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_mechanics_fragments, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setOnClickListeners()
        mechanicsRecyclerViewSetup()
        getMechanics()

    }

    private fun setOnClickListeners() {
        adminMechanicsAddFab.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun mechanicsRecyclerViewSetup() {
        mechanicAdapter = MechanicAdapter(mechanics, requireContext(), null, null, null)
        adminMechanicsRv.layoutManager = LinearLayoutManager(requireContext())
        adminMechanicsRv.adapter = mechanicAdapter
    }

    private fun getMechanics() {
        adminMechanicsProgressBar.makeVisible()
        mechanicsReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                showToast(requireContext(), p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (mechanics.size > 0) mechanics.clear()

                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val mechanic = tmp.getValue(MechanicModel::class.java)
                        mechanics.add(mechanic!!)
                        mechanicAdapter.notifyDataSetChanged()
                        adminMechanicsProgressBar.makeGone()
                    }
                }
            }

        })
    }

    private fun showBottomSheet() {
        bottomSheet = BottomSheetDialog(requireContext())
        sheetView = LayoutInflater.from(requireContext()).inflate(R.layout.sheet_add_mechanic, null, false)
        bottomSheet.setContentView(sheetView)
        bottomSheet.show()

        sheetView.newMechanicImg.setOnClickListener {
            openGallery()
        }

        sheetView.addNewMechanicBtn.setOnClickListener {
            val name = sheetView.sheetAddMechanicNameTxt.text.toString()
            val phone = sheetView.sheetAddMechanicPhoneTxt.text.toString()
            if (name.isEmpty() || phone.isEmpty()) {
                showToast(requireContext(), "Campos vacios")
            } else {
                progressDialogSetup()
                uploadMechanic(MechanicModel("", name, phone, ""))
            }
        }


    }

    private fun openGallery() {
        val intent = Intent()
        intent.setType("image/*")
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, ""), 1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //showToast(requireContext(), data.toString())
            Glide.with(requireContext()).load(Uri.parse(data?.data.toString())).apply(RequestOptions.circleCropTransform()).into(sheetView.newMechanicImg)
            uri = data?.data!!
        }
    }

    private fun uploadMechanic(mechanic: MechanicModel) {
        storageReference.child("photos").child(uri.lastPathSegment!!).putFile(uri)
            .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                    //showToast(baseContext, "Succeded")
                    storageReference.child("photos").child(uri.lastPathSegment!!).downloadUrl.addOnSuccessListener(object:
                        OnSuccessListener<Uri> {
                        override fun onSuccess(p0: Uri?) {
                            val id = (1000..10000).random().toString()
                            mechanic.mechanicId = id
                            mechanic.mechanicPhoto = p0.toString()
                            mechanicsReference.child(id).setValue(mechanic)
                            bottomSheet.dismiss()
                            progressDialog.dismiss()
                            //Dismiss dialog when upload finishes

                        }
                    })
                }
            })
            .addOnFailureListener(object: OnFailureListener {
                override fun onFailure(p0: Exception) {
                    p0.printStackTrace()
                }

            })
    }

    private fun progressDialogSetup() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setView(LayoutInflater.from(requireContext()).inflate(R.layout.dialog_progress, null, false))
        dialog.setCancelable(false)
        dialog.create()
        progressDialog = dialog.show()
    }
}

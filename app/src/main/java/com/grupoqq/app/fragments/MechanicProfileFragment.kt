package com.grupoqq.app.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.grupoqq.app.R
import com.grupoqq.app.activities.MechanicActivity
import com.grupoqq.app.models.MechanicModel
import com.grupoqq.app.utils.setGlideImage
import com.grupoqq.app.utils.showToast
import kotlinx.android.synthetic.main.fragment_mechanic_profile.*

class MechanicProfileFragment : Fragment() {

    private val mechanicsReference = FirebaseDatabase.getInstance().getReference("mechanics")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mechanic_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getMechanic()
    }

    private fun getMechanic() {
        mechanicsReference.child(MechanicActivity.mechanicId).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showToast(requireContext(), p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val mechanic = p0.getValue(MechanicModel::class.java)
                    setMechanicData(mechanic!!)
                } else {
                    showToast(requireContext(), "Reference does not exist")
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setMechanicData(mechanic: MechanicModel) {
        mechanicProfilePhotoImg.setGlideImage(requireContext(), mechanic.mechanicPhoto, circleCrop = true)
        mechanicProfileNameTxt.text = mechanic.mechanicNames
        mechanicProfilePhoneTxt.text = "Teléfono: ${mechanic.mechanicPhone}"
        mechanicProfileIdTxt.text = "Id: ${mechanic.mechanicId}"
    }


}

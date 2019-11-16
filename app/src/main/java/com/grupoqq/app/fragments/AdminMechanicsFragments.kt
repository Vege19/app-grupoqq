package com.grupoqq.app.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.grupoqq.app.R
import com.grupoqq.app.models.MechanicModel
import com.grupoqq.app.utils.GenericAdapter
import com.grupoqq.app.utils.MechanicAdapter
import com.grupoqq.app.utils.showToast
import kotlinx.android.synthetic.main.fragment_admin_mechanics_fragments.*

class AdminMechanicsFragments : Fragment() {

    private var mechanics = mutableListOf<MechanicModel>()
    private lateinit var mechanicAdapter: GenericAdapter<MechanicModel>
    private var mechanicsReference = FirebaseDatabase.getInstance().getReference("mechanics")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_mechanics_fragments, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mechanicsRecyclerViewSetup()
        getMechanics()

    }

    private fun mechanicsRecyclerViewSetup() {
        mechanicAdapter = MechanicAdapter(mechanics, requireContext(), null, null, null)
        adminMechanicsRv.layoutManager = LinearLayoutManager(requireContext())
        adminMechanicsRv.adapter = mechanicAdapter
    }

    private fun getMechanics() {
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
                    }
                }
            }

        })
    }

}

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
import com.grupoqq.app.activities.MechanicActivity
import com.grupoqq.app.models.BinnacleModel
import com.grupoqq.app.utils.BinnacleAdapter
import com.grupoqq.app.utils.GenericAdapter
import com.grupoqq.app.utils.showToast
import kotlinx.android.synthetic.main.fragment_mechanic_binnacles.*

class MechanicBinnaclesFragment : Fragment() {

    private var mechanicBinnacles = mutableListOf<BinnacleModel>()
    private lateinit var binnacleAdapter: GenericAdapter<BinnacleModel>
    private val binnaclesReference = FirebaseDatabase.getInstance().getReference("binnacles")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mechanic_binnacles, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mechanicBinnaclesRecyclerViewSetup()
        getMechanicBinnacles()

    }

    private fun mechanicBinnaclesRecyclerViewSetup() {
        binnacleAdapter = BinnacleAdapter(mechanicBinnacles, requireContext(), isMechanic = true)
        mechanicBinnaclesRv.layoutManager = LinearLayoutManager(requireContext())
        mechanicBinnaclesRv.adapter = binnacleAdapter
    }

    private fun getMechanicBinnacles() {
        binnaclesReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showToast(requireContext(), p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (mechanicBinnacles.size > 0) mechanicBinnacles.clear()

                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val binnacle = tmp.getValue(BinnacleModel::class.java)
                        if (binnacle?.mechanic?.mechanicId == MechanicActivity.mechanicId) {
                            mechanicBinnacles.add(binnacle)
                            binnacleAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }


}

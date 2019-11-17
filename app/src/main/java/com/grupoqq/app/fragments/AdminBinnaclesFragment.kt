package com.grupoqq.app.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.grupoqq.app.R
import com.grupoqq.app.activities.NewBinnacleActivity
import com.grupoqq.app.models.BinnacleModel
import com.grupoqq.app.utils.BinnacleAdapter
import com.grupoqq.app.utils.GenericAdapter
import com.grupoqq.app.utils.showToast
import kotlinx.android.synthetic.main.fragment_admin_binnacles.*
import kotlinx.android.synthetic.main.layout_actionbar.view.*

class AdminBinnaclesFragment : Fragment() {

    private lateinit var binnacleAdapter: GenericAdapter<BinnacleModel>
    private var binnacles = mutableListOf<BinnacleModel>()
    private var binnacleReference = FirebaseDatabase.getInstance().getReference("binnacles")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_binnacles, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setOnClickListener()
        binnacleRecyclerViewSetup()
        getBinnacles()

    }

    private fun setOnClickListener() {
        //Fab
        adminNewBinnacleFab.setOnClickListener {
            startActivity(Intent(requireContext(), NewBinnacleActivity::class.java))
        }
    }

    private fun binnacleRecyclerViewSetup() {
        binnacleAdapter = BinnacleAdapter(binnacles, requireContext())
        adminBinnaclesRv.layoutManager = LinearLayoutManager(requireContext())
        adminBinnaclesRv.adapter = binnacleAdapter
    }

    private fun getBinnacles() {
        binnacleReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                showToast(requireContext(), p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (binnacles.size > 0) binnacles.clear()

                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val binnacle = tmp.getValue(BinnacleModel::class.java)
                        binnacles.add(binnacle!!)
                        binnacleAdapter.notifyDataSetChanged()
                    }
                }
            }

        })
    }

}

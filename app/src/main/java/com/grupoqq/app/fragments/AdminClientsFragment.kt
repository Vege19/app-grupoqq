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
import com.grupoqq.app.models.ClientModel
import com.grupoqq.app.utils.ClientAdapter
import com.grupoqq.app.utils.GenericAdapter
import com.grupoqq.app.utils.showToast
import kotlinx.android.synthetic.main.fragment_admin_clients.*
import kotlinx.android.synthetic.main.item_client.*

class AdminClientsFragment : Fragment() {

    private var clients = mutableListOf<ClientModel>()
    private lateinit var clientAdapter: GenericAdapter<ClientModel>
    private var clientsReference = FirebaseDatabase.getInstance().getReference("clients")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_clients, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        clientsRecyclerViewSetup()
        getClients()

    }

    private fun clientsRecyclerViewSetup() {
        clientAdapter = ClientAdapter(clients, requireContext())
        adminClientsRv.layoutManager = LinearLayoutManager(requireContext())
        adminClientsRv.adapter = clientAdapter
    }

    private fun getClients() {
        clientsReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                showToast(requireContext(), p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (clients.size > 0) clients.clear()

                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val client = tmp.getValue(ClientModel::class.java)
                        clients.add(client!!)
                        clientAdapter.notifyDataSetChanged()
                    }
                }
            }

        })
    }

}

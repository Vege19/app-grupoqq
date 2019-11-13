package com.grupoqq.app.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.grupoqq.app.BinnacleModel

import com.grupoqq.app.R
import com.grupoqq.app.utils.getFirebaseReference
import kotlinx.android.synthetic.main.fragment_search_binnacle.*

class SearchBinnacleFragment : Fragment() {

    private lateinit var binnacleCode: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_binnacle, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setOnClickListeners()

    }

    private fun setOnClickListeners() {
        searchBinnacleToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        requestBinnacleBtn.setOnClickListener {
            binnacleCode = binnacleCodeInputTxt.text.toString().trim()
            if (binnacleCode.isNotEmpty()) {
                searchBinnacle()
            } else {
                Log.d("Debug", "Empty field.")
            }
        }
    }

    private fun searchBinnacle() {
        getFirebaseReference("binnacle").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val binnacle = tmp.getValue(BinnacleModel::class.java)
                        if (binnacle?.binnacleId == binnacleCode) {

                            break
                        } else {
                            Log.d("Debug", "Referencia no encontrada.")
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("Debug", p0.message)
            }
        })
    }

}

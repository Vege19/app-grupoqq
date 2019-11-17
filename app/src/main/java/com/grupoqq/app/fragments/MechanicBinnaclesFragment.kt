package com.grupoqq.app.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.grupoqq.app.R
import com.grupoqq.app.models.BinnacleModel
import com.grupoqq.app.utils.GenericAdapter

class MechanicBinnaclesFragment : Fragment() {

    private var mechanicBinnacles = mutableListOf<BinnacleModel>()
    private lateinit var binnacleAdapter: GenericAdapter<BinnacleModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mechanic_binnacles, container, false)
    }

    private fun mechanicBinnaclesRecyclerViewSetup() {

    }


}

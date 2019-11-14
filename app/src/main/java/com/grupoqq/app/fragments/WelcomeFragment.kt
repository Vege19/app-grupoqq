package com.grupoqq.app.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.grupoqq.app.R
import com.grupoqq.app.activities.NewBinnacleActivity
import kotlinx.android.synthetic.main.fragment_search_binnacle.*
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setOnClickListeners()

    }

    private fun setOnClickListeners() {
        cardSelectionOne.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_searchBinnacleFragment)
        }

        cardSelectionTwo.setOnClickListener {
            startActivity(Intent(activity, NewBinnacleActivity::class.java))
        }
    }

}

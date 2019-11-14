package com.grupoqq.app.fragments


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.grupoqq.app.R
import com.grupoqq.app.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_new_binnacle.*
import java.lang.reflect.Array
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class NewBinnacle : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_binnacle, container, false)
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)


    }

}

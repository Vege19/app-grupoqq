package com.grupoqq.app.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import com.grupoqq.app.R
import com.grupoqq.app.models.BinnacleRepairModel
import com.grupoqq.app.models.ReportModel
import com.grupoqq.app.utils.GenericAdapter
import com.grupoqq.app.utils.getFirebaseReference
import com.grupoqq.app.utils.setGlideImage
import kotlinx.android.synthetic.main.fragment_repair_details.*
import kotlinx.android.synthetic.main.item_report.view.*

class RepairDetailsFragment : Fragment() {

    private var mBinnacleRepairId = 0
    private lateinit var mBinnacleRepair: BinnacleRepairModel
    private var mReports = mutableListOf<ReportModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repair_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fetchArguments()
        setOnClickListeners()
        getReports()
        setReportsRecyclerView()
        loadBinnacleData()

    }

    private fun fetchArguments() {
        mBinnacleRepairId = arguments?.getInt("BINNACLE_REPAIR_ID_KEY")!!
        mBinnacleRepair = arguments?.getSerializable("REPAIR_KEY") as BinnacleRepairModel
    }

    private fun setOnClickListeners() {
        repairDetailsToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getReports() {
        getFirebaseReference("binnacle/0/repairs/$mBinnacleRepairId/reports").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (mReports.size > 0) mReports.clear()

                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val report = tmp.getValue(ReportModel::class.java)
                        mReports.add(report!!)
                        setReportsRecyclerView()
                        reportsAdapter().notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUG", p0.message)
            }
        })

    }

    private fun setReportsRecyclerView() {
        reportsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        reportsRecyclerView.adapter = reportsAdapter()
    }

    private fun reportsAdapter(): GenericAdapter<ReportModel> {
        return GenericAdapter(R.layout.item_report, mReports, fun (viewHolder, view, report, _) {
            if (mReports.isNotEmpty()) {
                view.reportDescriptionTxt.text = report.reportDescription
                view.reportDateTimeTxt.text = report.reportDateTime
                view.reportPictureImg.setGlideImage(requireContext(), report.reportPicture)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun loadBinnacleData() {
       // repairDetailNameTxt.text = mBinnacleRepair.repair?.repairName
        if (mBinnacleRepair.isApproved) {
            repairDetailStartDateTxt.text = "Fecha de inicio: ${mBinnacleRepair.binnacleRepairStartDate}"
        } else {
            repairDetailStartDateTxt.text = "Sin aprobar"
        }
        var statusString = ""
        statusString = when (mBinnacleRepair.binnacleRepairStatus) {
            1 -> "Pendiente"
            2 -> "En progreso"
            else -> "Finalizado"
        }
        repairDetailStatusTxt.text = "Estado: $statusString"
    }

}

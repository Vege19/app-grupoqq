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
import com.grupoqq.app.models.BinnacleModel
import com.grupoqq.app.models.BinnacleRepairModel
import com.grupoqq.app.models.ReportModel
import com.grupoqq.app.utils.GenericAdapter
import com.grupoqq.app.utils.getFirebaseReference
import com.grupoqq.app.utils.setGlideImage
import kotlinx.android.synthetic.main.fragment_repair_details.*
import kotlinx.android.synthetic.main.item_report.view.*

class RepairDetailsFragment : Fragment() {

    private var binnacleRepairId = ""
    private var mReports = mutableListOf<ReportModel>()
    private lateinit var mReportsAdapter: GenericAdapter<ReportModel>
    private var repairName = ""
    private var binnacleId = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_repair_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fetchArguments()
        setOnClickListeners()
        getBinnacle()
        setReportsRecyclerView()
        getReports()

    }

    private fun fetchArguments() {
        binnacleRepairId = arguments?.getString("BINNACLE_REPAIR_ID_KEY")!!
        repairName = arguments?.getString("REPAIR_NAME_KEY")!!
        binnacleId = arguments?.getString("BINNACLE_ID_KEY")!!
    }

    private fun setOnClickListeners() {
        repairDetailsToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getReports() {
        getFirebaseReference("binnacle/$binnacleId/repairs/$binnacleRepairId/reports").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (mReports.size > 0) mReports.clear()

                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val report = tmp.getValue(ReportModel::class.java)
                        mReports.add(report!!)
                        mReportsAdapter.notifyDataSetChanged()
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
        mReportsAdapter = reportsAdapter()
        reportsRecyclerView.adapter = mReportsAdapter
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
    private fun getBinnacle() {
        getFirebaseReference("binnacle/$binnacleId/repairs/$binnacleRepairId").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    loadBinnacleData(p0.getValue(BinnacleRepairModel::class.java)!!)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d("DEBUG", p0.message)
            }
        })
    }

    private fun loadBinnacleData(binnacleRepair: BinnacleRepairModel) {
        repairDetailNameTxt.text = repairName
        if (binnacleRepair.isApproved!!) {
            repairDetailStartDateTxt.text = "Fecha de inicio: ${binnacleRepair.binnacleRepairStartDate}"
        } else {
            repairDetailStartDateTxt.text = "Sin aprobar"
        }
        var statusString = ""
        statusString = when (binnacleRepair.binnacleRepairStatus) {
            1 -> "Pendiente"
            2 -> "En progreso"
            else -> "Finalizado"
        }
        repairDetailStatusTxt.text = "Estado: $statusString"
    }
}

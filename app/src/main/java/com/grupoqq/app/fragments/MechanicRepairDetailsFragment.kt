package com.grupoqq.app.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

class MechanicRepairDetailsFragment : Fragment() {

    private lateinit var mBinnacle: BinnacleModel
    private lateinit var mRepair: BinnacleRepairModel
    private var mReports = mutableListOf<ReportModel>()
    private var mReportAdapter = reportsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_repair_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addNewReportFab.visibility = View.VISIBLE
        repairDetailInstructionTxt.text = "¡Reporta al cliente tu trabajo! Cada vez que termines un proceso, informale al cliente agregando un nuevo reporte."
        setOnClickListeners()
        fetchArguments()
        setBinnacleRepairData()
        setReportsRecyclerView()
        getReports()

    }

    private fun fetchArguments() {
        mBinnacle = arguments?.getSerializable("BINNACLE_KEY") as BinnacleModel
        mRepair = arguments?.getSerializable("REPAIR_KEY") as BinnacleRepairModel
    }

    private fun setOnClickListeners() {
        repairDetailsToolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setBinnacleRepairData() {
        //Repair name
        getFirebaseReference("repair/${mRepair.repairId}/repairName").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Debug", p0.message)
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    repairDetailNameTxt.text = p0.getValue(String::class.java)
                } else {
                    Log.d("Debug", "Reference does not exist.")
                }
            }
        })

        //Repair date and status
        getFirebaseReference("binnacle/${mBinnacle.binnacleId}/repairs/${mRepair.binnacleRepairId}").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val repair = p0.getValue(BinnacleRepairModel::class.java)
                    repairDetailStatusTxt.text = when (repair?.binnacleRepairStatus) {
                        1 -> "Pendiente"
                        2 -> "En progreso"
                        else -> "Completado"
                    }
                    if (repair!!.isApproved) {
                        repairDetailStartDateTxt.text = repair.binnacleRepairStartDate
                    } else {
                        repairDetailStartDateTxt.text = "Sin aprobar"
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Debug", p0.message)
            }
        })
    }

    private fun getReports() {
        getFirebaseReference("binnacle/${mBinnacle.binnacleId}/repairs/${mRepair.binnacleRepairId}/reports").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Debug", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (mReports.size > 0) mReports.clear()

                if (p0.exists()) {
                    for (tmp in p0.children) {
                        val repair = tmp.getValue(ReportModel::class.java)
                        mReports.add(repair!!)
                        mReportAdapter.notifyDataSetChanged()
                    }
                }
            }

        })
    }

    private fun setReportsRecyclerView() {
        reportsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        reportsRecyclerView.adapter = mReportAdapter
    }

    private fun reportsAdapter(): GenericAdapter<ReportModel> {
        return GenericAdapter(R.layout.item_report, mReports, fun (viewHolder, view, report, _) {
            view.reportPictureImg.setGlideImage(requireContext(), report.reportPicture)
            view.reportDescriptionTxt.text = report.reportDescription
            view.reportDateTimeTxt.text = report.reportDateTime
        })
    }


}

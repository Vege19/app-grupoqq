package com.grupoqq.app.models

import java.io.Serializable

data class BinnacleRepairModel(
    val binnacleRepairId: String = "",
    val repairId: String = "",
    val binnacleRepairStatus: Int = 0,
    val binnacleRepairStartDate: String = "",
    val isApproved: Boolean = false,
    val reports: HashMap<String, ReportModel> = HashMap(),
    val binnacleSpareParts: List<SparePartModel> = arrayListOf()
) : Serializable
package com.grupoqq.app.models

import java.io.Serializable

data class BinnacleRepairModel(
    val binnacleRepairId: Int = 0,
    val repair: RepairModel? = null,
    val repairStatus: Int = 0,
    val repairStartDate: String = "",
    val isApproved: Boolean = false,
    val reports: List<ReportModel> = arrayListOf(),
    val spareParts: List<SparePartModel> = arrayListOf()
) : Serializable
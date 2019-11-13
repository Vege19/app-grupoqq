package com.grupoqq.app.models

data class BinnacleRepairModel(
    val repair: RepairModel? = null,
    val repairStatus: Int = 0,
    val repairStartDate: String = "",
    val isApproved: Boolean = false,
    val processes: List<ProcessModel> = arrayListOf(),
    val spareParts: List<SparePartModel> = arrayListOf()
)
package com.grupoqq.app.models

import java.io.Serializable

data class BinnacleModel(
    val binnacleId: String = "",
    val clientId: String = "",
    val mechanicId: String = "",
    val vehicleId: String = "",
    val repairs: List<BinnacleRepairModel> = arrayListOf()
    //val repairs: List<String> = arrayListOf()
) : Serializable
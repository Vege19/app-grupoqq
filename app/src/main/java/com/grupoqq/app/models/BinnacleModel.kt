package com.grupoqq.app.models

import java.io.Serializable

data class BinnacleModel(
    val binnacleId: String = "",
    val clientId: Int = 0,
    val mechanicId: Int = 0,
    val vehicleId: Int = 0
    //val repairs: List<String> = arrayListOf()
) : Serializable
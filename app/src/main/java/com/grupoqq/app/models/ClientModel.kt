package com.grupoqq.app.models

import java.io.Serializable

data class ClientModel(
    val clientId: Int = 0,
    val clientMail: String = "",
    val clientNames: String = "",
    val clientPhone: String = "",
    val vehicles: List<VehicleModel> = arrayListOf(),
    val binnacles: List<BinnacleModel> = arrayListOf()
) : Serializable
package com.grupoqq.app.models

import java.io.Serializable

data class VehicleModel(
    val vehicleId: Int = 0,
    val vehicleBrand: String = "",
    val vehicleModel: String = "",
    val vehicleRegistrationNumber: String = "",
    val vehicleYear: String = "",
    val clientId: Int = 0
) : Serializable
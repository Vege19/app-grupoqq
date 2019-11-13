package com.grupoqq.app.models

import java.io.Serializable

data class VehicleModel(
    val vehicleId: String = "",
    val vehicleBrand: String = "",
    val vehicleModel: String = "",
    val vehicleRegistrationNumber: String = "",
    val vehicleYear: String = "",
    val clientId: String = ""
) : Serializable
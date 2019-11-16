package com.grupoqq.app.models

data class VehicleModel(
    var vehicleId: String = "",
    var vehicleBrand: String = "",
    var vehicleModel: String = "",
    var vehicleYear: String = "",
    var vehicleRegistrationNumber: String = "",
    var client: ClientModel = ClientModel()
)
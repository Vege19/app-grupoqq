package com.grupoqq.app.models

data class ClientModel(
    var clientId: String = "",
    var clientNames: String = "",
    var clientEmail: String = "",
    var clientPhone: String = "",
    var vehicles: List<VehicleModel> = arrayListOf(),
    var binnacles: List<BinnacleModel> = arrayListOf()
)
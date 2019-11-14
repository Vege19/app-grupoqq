package com.grupoqq.app.models

class BinnacleModel(
    var binnacleId: String = "",
    var client: ClientModel = ClientModel(),
    var vehicle: VehicleModel = VehicleModel(),
    var mechanic: MechanicModel = MechanicModel(),
    var services: List<ServiceModel> = arrayListOf()
)
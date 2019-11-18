package com.grupoqq.app.models

data class BinnacleModel(
    var binnacleId: String = "",
    var client: ClientModel = ClientModel(),
    var vehicle: VehicleModel = VehicleModel(),
    var mechanic: MechanicModel = MechanicModel(),
    var binnacleServices: HashMap<String, BinnacleServiceModel> = HashMap(),
    var binnacleQuotation: List<QuotationModel> = arrayListOf()
)
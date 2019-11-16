package com.grupoqq.app.models

data class BinnacleServiceModel(
    var binnacleServiceId: String = "",
    var binnacleServiceStatus: Int = 0,
    var binnacleServiceStartDate: String = "",
    var binnacleServiceEndDate: String = "",
    var service: ServiceModel = ServiceModel()
)
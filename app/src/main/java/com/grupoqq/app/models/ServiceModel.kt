package com.grupoqq.app.models

data class ServiceModel (
    var serviceId: String = "",
    var serviceName: String = "",
    var serviceCost: Double = 0.0,
    var isChecked: Boolean = false
)
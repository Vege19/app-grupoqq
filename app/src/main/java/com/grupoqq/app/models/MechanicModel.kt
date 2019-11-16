package com.grupoqq.app.models

data class MechanicModel(
    var mechanicId: String = "",
    var mechanicNames: String = "",
    var mechanicPhone: String = "",
    var mechanicPhoto: String = "",
    var binnacles: List<BinnacleModel> = arrayListOf()
)
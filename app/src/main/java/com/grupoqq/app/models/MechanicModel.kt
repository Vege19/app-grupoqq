package com.grupoqq.app.models

import java.io.Serializable

data class MechanicModel(
    val mechanicId: String = "",
    val mechanicNames: String = "",
    val mechanicProfile: String = "",
    val binnacles: List<BinnacleModel> = arrayListOf()
) : Serializable
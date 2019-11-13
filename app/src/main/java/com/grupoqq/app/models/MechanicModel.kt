package com.grupoqq.app.models

import java.io.Serializable

data class MechanicModel(
    val mechanicId: String = "",
    val mechanicNames: String = "",
    val clients: List<ClientModel> = arrayListOf(),
    val mechanicProfile: String = ""
) : Serializable
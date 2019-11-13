package com.grupoqq.app.models

import java.io.Serializable

data class RepairModel(
    val repairId: Int = 0,
    val repairName: String = ""
) : Serializable
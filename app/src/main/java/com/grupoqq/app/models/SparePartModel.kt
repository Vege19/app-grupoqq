package com.grupoqq.app.models

import java.io.Serializable

data class SparePartModel(
    val sparePartId: String = "",
    val sparePartName: String = "",
    val sparePartCost: Double = 0.0
) : Serializable
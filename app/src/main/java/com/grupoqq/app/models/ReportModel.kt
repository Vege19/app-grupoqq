package com.grupoqq.app.models

import java.io.Serializable

data class ReportModel(
    val reportDescription: String = "",
    val reportPicture: String = "",
    val reportDateTime: String = ""
) : Serializable
package com.grupoqq.app.models

import java.io.Serializable

data class ReportModel(
    var reportId: String = "",
    var reportDescription: String = "",
    var reportPicture: String = "",
    var reportDateTime: String = ""
) : Serializable
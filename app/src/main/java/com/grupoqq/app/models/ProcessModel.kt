package com.grupoqq.app.models

import java.io.Serializable

data class ProcessModel(
    val processDescription: String = "",
    val processPicture: String = "",
    val processTimeDate: String = ""
) : Serializable
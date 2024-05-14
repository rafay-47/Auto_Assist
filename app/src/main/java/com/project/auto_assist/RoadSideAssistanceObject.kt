package com.project.auto_assist


data class RoadSideAssistanceObject(
    val serviceId: String = "",
    val userId: String = "",
    val name: String = "",
    val mobile: String = "",
    val location: String = "",
    val carBrand: String = "",
    val carModel: String = "",
    val carVariant: String = "",
    val serviceDetail: String = "",
    val description: String = "",
    val imageUri: String = "",
    var status: String = "pending"
)

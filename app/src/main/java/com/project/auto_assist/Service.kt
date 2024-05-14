package com.project.auto_assist

data class Service(
    val serviceId: String = "",
    val userId: String = "",
    val workShopID: String = "",
    val name: String = "",
    val mobile: String = "",
    val carBrand: String = "",
    val carModel: String = "",
    val carVariant: String = "",
    val serviceDetail: String = "",
    val description: String = "",
    val imageUri: String = "",
    val status: String = "Booked"
)

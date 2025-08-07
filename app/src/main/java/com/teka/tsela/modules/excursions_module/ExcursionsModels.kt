package com.teka.tsela.modules.excursions_module

data class ExcursionsApiResponse(
    val excursions: List<ExcursionResponse>,
    val count: Int
)

data class ExcursionResponse(
    val id: String,
    val name: String,
    val description: String,
    val location: String,
    val duration: String,
    val price_resident: Int?,
    val price_nonresident: String?,
    val includes: List<String>,
    val notes: String?,
    val category: String,
    val image_url: String,
    val created_at: String,
    val updated_at: String,
    val country: String,
    val county: String,
    val images: List<String>,
    val excursion_packages: List<ExcursionPackage>,
    val excursion_activities: List<ExcursionActivity>
)

data class ExcursionPackage(
    val id: String,
    val name: String,
    val duration: String,
    val includes: List<String>,
    val created_at: String,
    val excursion_id: String,
    val price_resident: Int,
    val price_nonresident: String
)

data class ExcursionActivity(
    val id: String,
    val name: String,
    val description: String?,
    val duration: String?,
    val price_resident: Int?,
    val price_nonresident: String?,
    val created_at: String,
    val excursion_id: String
)
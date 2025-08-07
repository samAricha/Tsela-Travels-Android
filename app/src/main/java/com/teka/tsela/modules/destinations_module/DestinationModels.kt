package com.teka.tsela.modules.destinations_module

import kotlinx.serialization.Serializable

@Serializable
data class DestinationResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val country: String,
    val region: String,
    val popularity_score: Int,
    val image_url: String,
    val created_at: String,
    val updated_at: String,
    val images: List<String> = emptyList()
)

@Serializable
data class DestinationsApiResponse(
    val destinations: List<DestinationResponse>,
    val total: Int
)
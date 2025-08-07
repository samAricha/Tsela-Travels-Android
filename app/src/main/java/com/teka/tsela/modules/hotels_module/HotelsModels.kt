package com.teka.tsela.modules.hotels_module

import kotlinx.serialization.Serializable

@Serializable
data class HotelsResponse(
    val hotels: List<HotelResponse>,
    val total: Int,
    val offset: Int,
    val limit: Int
)

@Serializable
data class HotelResponse(
    val id: String,
    val name: String,
    val chain_name: String? = null,
    val location: String,
    val country: String,
    val city: String,
    val description: String,
    val star_rating: Int,
    val image_url: String,
    val contact_phone: String,
    val contact_email: String,
    val website: String,
    val price_per_night: Int,
    val amenities: List<String>,
    val featured: Boolean,
    val room_types: List<RoomType>,
    val contact_info: ContactInfo,
    val pricing: List<String> // Can be refined based on actual pricing structure
)

@Serializable
data class RoomType(
    val id: String,
    val name: String,
    val category: String,
    val max_occupancy: Int,
    val amenities: List<String>,
    val image_url: String
)

@Serializable
data class ContactInfo(
    val phone: String,
    val email: String,
    val address: String
)
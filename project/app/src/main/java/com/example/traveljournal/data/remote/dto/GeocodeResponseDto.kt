package com.example.traveljournal.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodeResponseDto(
    @Json(name = "results")
    val results: List<ResultDto>? = null,
    @Json(name = "status")
    val status: String? = null
)

@JsonClass(generateAdapter = true)
data class ResultDto(
    @Json(name = "address_components")
    val addressComponents: List<AddressComponentDto>? = null,
    @Json(name = "formatted_address")
    val formattedAddress: String? = null,
    @Json(name = "geometry")
    val geometry: GeometryDto? = null,
    @Json(name = "place_id")
    val placeId: String? = null,
    @Json(name = "types")
    val types: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class AddressComponentDto(
    @Json(name = "long_name")
    val longName: String? = null,
    @Json(name = "short_name")
    val shortName: String? = null,
    @Json(name = "types")
    val types: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class GeometryDto(
    @Json(name = "location")
    val location: LocationDto? = null,
    @Json(name = "location_type")
    val locationType: String? = null,
    @Json(name = "viewport")
    val viewport: ViewportDto? = null
)

@JsonClass(generateAdapter = true)
data class LocationDto(
    @Json(name = "lat")
    val lat: Double? = null,
    @Json(name = "lng")
    val lng: Double? = null
)

@JsonClass(generateAdapter = true)
data class ViewportDto(
    @Json(name = "northeast")
    val northeast: LocationDto? = null,
    @Json(name = "southwest")
    val southwest: LocationDto? = null
)
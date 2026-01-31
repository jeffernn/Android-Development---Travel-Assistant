package com.example.traveljournal.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeRate(
    val currency: String,
    val rate: Double
)

@JsonClass(generateAdapter = true)
data class ExchangeRateResponse(
    val code: Int,
    val message: String,
    val data: ExchangeRateData
)

@JsonClass(generateAdapter = true)
data class ExchangeRateData(
    @Json(name = "base_code")
    val baseCode: String,
    val updated: String,
    @Json(name = "updated_at")
    val updatedAt: Long,
    @Json(name = "next_updated")
    val nextUpdated: String,
    @Json(name = "next_updated_at")
    val nextUpdatedAt: Long,
    val rates: List<ExchangeRateItem>
)

@JsonClass(generateAdapter = true)
data class ExchangeRateItem(
    val currency: String,
    val rate: Double
)
package com.example.traveljournal.util

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LanguageItem(
    val code: String,
    val label: String
) {
    override fun toString(): String {
        return label
    }
}

@JsonClass(generateAdapter = true)
data class TranslationResponse(
    val code: Int,
    val message: String,
    @Json(name = "data")
    val translationData: TranslationData?
)

@JsonClass(generateAdapter = true)
data class TranslationData(
    val source: SourceData,
    val target: TargetData
)

@JsonClass(generateAdapter = true)
data class SourceData(
    val text: String,
    val type: String,
    @Json(name = "type_desc")
    val typeDesc: String,
    val pronounce: String?
)

@JsonClass(generateAdapter = true)
data class TargetData(
    val text: String,
    val type: String,
    @Json(name = "type_desc")
    val typeDesc: String,
    val pronounce: String?
)
package com.example.traveljournal.data.remote

import com.example.traveljournal.util.LanguageItem
import com.example.traveljournal.util.TranslationResponse
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslationApiService {
    @GET("v2/fanyi")
    suspend fun translate(
        @Query("text") text: String,
        @Query("from") from: String = "auto",
        @Query("to") to: String = "auto",
        @Query("encoding") encoding: String = "json"
    ): Response<TranslationResponse>

    @GET("v2/fanyi/langs")
    suspend fun getSupportedLanguages(): Response<LanguageResponse>
}

@JsonClass(generateAdapter = true)
data class LanguageResponse(
    val code: Int,
    val message: String,
    val data: List<LanguageItem>
)
package com.example.traveljournal.data.remote.api

import com.example.traveljournal.data.remote.dto.GeocodeResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeApiService {
    /**
     * 根据地址获取地理坐标
     */
    @GET("geocode/json")
    suspend fun geocodeAddress(
        @Query("address") address: String,
        @Query("key") apiKey: String
    ): Response<GeocodeResponseDto>
    
    /**
     * 根据经纬度反向地理编码获取地址
     */
    @GET("geocode/json")
    suspend fun reverseGeocode(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String
    ): Response<GeocodeResponseDto>
}
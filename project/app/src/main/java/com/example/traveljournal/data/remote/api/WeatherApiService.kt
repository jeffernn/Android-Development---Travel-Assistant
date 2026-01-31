package com.example.traveljournal.data.remote.api

import com.example.traveljournal.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    /**
     * 获取天气预报数据
     * @param query 待查询地区
     * @param days 预报天数，默认为7天，最大支持8天
     * @param encoding 编码方式，支持 text/json/markdown
     */
    @GET("v2/weather/forecast")
    suspend fun getWeatherForecast(
        @Query("query") query: String? = null,
        @Query("days") days: Int = 7,
        @Query("encoding") encoding: String = "json"
    ): Response<WeatherResponse>
}
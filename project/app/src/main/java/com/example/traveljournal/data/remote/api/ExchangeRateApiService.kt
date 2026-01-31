package com.example.traveljournal.data.remote.api

import com.example.traveljournal.data.model.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeRateApiService {
    /**
     * 获取汇率数据
     * @param currency 货币代码，默认为CNY
     * @param encoding 编码格式
     */
    @GET("v2/exchange-rate")
    suspend fun getExchangeRates(
        @Query("currency") currency: String = "CNY",
        @Query("encoding") encoding: String = "json"
    ): Response<ExchangeRateResponse>
}
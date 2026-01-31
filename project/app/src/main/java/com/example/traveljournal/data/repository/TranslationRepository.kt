package com.example.traveljournal.data.repository

import com.example.traveljournal.data.remote.TranslationApiService
import com.example.traveljournal.util.LanguageItem
import com.example.traveljournal.util.Resource
import com.example.traveljournal.util.TranslationResponse
import javax.inject.Inject

class TranslationRepository @Inject constructor(
    private val apiService: TranslationApiService
) {
    suspend fun translate(text: String, from: String = "auto", to: String = "auto"): Resource<TranslationResponse> {
        return try {
            val response = apiService.translate(text, from, to)
            if (response.isSuccessful) {
                response.body()?.let { 
                    Resource.Success(it) 
                } ?: Resource.Error("响应体为空")
            } else {
                Resource.Error("翻译请求失败: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("翻译请求异常: ${e.message}")
        }
    }

    suspend fun getSupportedLanguages(): Resource<List<LanguageItem>> {
        return try {
            val response = apiService.getSupportedLanguages()
            if (response.isSuccessful) {
                response.body()?.let { languageResponse ->
                    if (languageResponse.code == 200) {
                        Resource.Success(languageResponse.data)
                    } else {
                        Resource.Error("获取语言列表失败: ${languageResponse.message}")
                    }
                } ?: Resource.Error("响应体为空")
            } else {
                Resource.Error("获取语言列表失败: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("获取语言列表异常: ${e.message}")
        }
    }
}
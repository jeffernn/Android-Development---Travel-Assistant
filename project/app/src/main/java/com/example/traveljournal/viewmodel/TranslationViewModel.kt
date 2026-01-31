package com.example.traveljournal.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveljournal.data.repository.TranslationRepository
import com.example.traveljournal.util.LanguageItem
import com.example.traveljournal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranslationViewModel @Inject constructor(
    private val repository: TranslationRepository
) : ViewModel() {
    
    var sourceText by mutableStateOf("")
        private set
    
    var translatedText by mutableStateOf("")
        private set
    
    var isTranslating by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf("")
        private set
    
    var sourceLanguage by mutableStateOf(LanguageItem("auto", "自动检测"))
        private set
    
    var targetLanguage by mutableStateOf(LanguageItem("en", "英文"))
        private set
    
    var supportedLanguages by mutableStateOf<List<LanguageItem>>(emptyList())
        private set
    
    var languagesLoaded by mutableStateOf(false)
        private set

    fun updateSourceText(text: String) {
        sourceText = text
    }

    fun updateSourceLanguage(language: LanguageItem) {
        sourceLanguage = language
    }

    fun updateTargetLanguage(language: LanguageItem) {
        targetLanguage = language
    }

    fun translate() {
        if (sourceText.isBlank()) {
            errorMessage = "请输入要翻译的文本"
            return
        }

        viewModelScope.launch {
            isTranslating = true
            errorMessage = ""
            
            when (val result = repository.translate(
                text = sourceText,
                from = sourceLanguage.code,
                to = targetLanguage.code
            )) {
                is Resource.Success -> {
                    if (result.data?.code == 200 && result.data.translationData != null) {
                        translatedText = result.data.translationData.target.text
                    } else {
                        errorMessage = result.data?.message ?: "翻译失败"
                    }
                }
                is Resource.Error -> {
                    errorMessage = result.message
                }
                is Resource.Loading -> {
                    // 翻译状态已经在上面设置
                }
            }
            
            isTranslating = false
        }
    }

    fun loadSupportedLanguages() {
        viewModelScope.launch {
            when (val result = repository.getSupportedLanguages()) {
                is Resource.Success -> {
                    supportedLanguages = result.data
                    languagesLoaded = true
                }
                is Resource.Error -> {
                    errorMessage = result.message
                    languagesLoaded = false
                }
                is Resource.Loading -> {
                    // 正在加载
                }
            }
        }
    }
}
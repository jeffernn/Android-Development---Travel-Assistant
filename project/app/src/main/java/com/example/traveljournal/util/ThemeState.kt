package com.example.traveljournal.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.Flow

@Composable
fun observeDarkTheme(): State<Boolean> {
    val context = LocalContext.current
    val themeManager = ThemeManager(context)
    
    return produceState(initialValue = false) {
        themeManager.isDarkTheme.collect { isDark ->
            value = isDark
        }
    }
}
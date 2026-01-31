package com.example.traveljournal.ui.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument

object NavigationArgs {
    const val TRIP_ID_ARG = "tripId"
    
    fun NavBackStackEntry.getTripId(): Long {
        return arguments?.getLong(TRIP_ID_ARG) ?: 0L
    }
}

// 用于导航参数定义
val tripDetailArguments = listOf(
    navArgument(NavigationArgs.TRIP_ID_ARG) {
        type = NavType.LongType
        defaultValue = 0L
    }
)

val editTripArguments = listOf(
    navArgument(NavigationArgs.TRIP_ID_ARG) {
        type = NavType.LongType
        defaultValue = 0L
    }
)
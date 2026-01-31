package com.example.traveljournal.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(var route: String, var icon: ImageVector, var title: String) {
    object Home : NavigationItem("home", Icons.Default.Home, "首页")
    object ExchangeRate : NavigationItem("exchange_rate", Icons.Default.Settings, "汇率")
    object Translation : NavigationItem("translation", Icons.Default.Edit, "翻译")
    object Weather : NavigationItem("weather", Icons.Default.Star, "天气")
    object AddTrip : NavigationItem("add_trip", Icons.Default.Add, "添加旅行")
    object TripDetail : NavigationItem("trip_detail/{tripId}", Icons.Default.Info, "旅行详情")
    object EditTrip : NavigationItem("edit_trip/{tripId}", Icons.Default.Edit, "编辑旅行")
}
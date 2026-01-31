package com.example.traveljournal.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import com.example.traveljournal.ui.screens.AddTripScreen
import com.example.traveljournal.ui.screens.ExchangeRateScreen
import com.example.traveljournal.ui.screens.HomeScreenWithFab
import com.example.traveljournal.ui.screens.TripDetailScreen
import com.example.traveljournal.ui.screens.EditTripScreen
import com.example.traveljournal.ui.screens.TranslationScreen
import com.example.traveljournal.ui.screens.WeatherScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupNavigation() {
    val navController = rememberNavController()

    // 为底部导航定义的屏幕
    val bottomNavScreens = listOf(
        NavigationItem.Home,
        NavigationItem.ExchangeRate,
        NavigationItem.Translation,
        NavigationItem.Weather
    )

    val currentDestination by navController.currentBackStackEntryAsState()

    // 检查当前是否在底部导航的屏幕上
    val showBottomBar = bottomNavScreens.any { it.route == currentDestination?.destination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                // 使用Row构建自定义底部导航栏，中间放置添加按钮，文字在中间高度显示
                Surface(
                    color = MaterialTheme.colorScheme.surface, // 使用主题颜色
                    tonalElevation = 0.dp // 移除阴影
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp), // 增加高度
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 左侧导航项（仅文字）
                        NavigationItem.Home.let { screen ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable {
                                        navController.navigate(screen.route) {
                                            // 避免重复添加相同的路线到返回栈
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = screen.title,
                                    fontSize = 16.sp, // 增大文字大小
                                    color = if (currentDestination?.destination?.route == screen.route) {
                                        MaterialTheme.colorScheme.primary // 使用主题主色调
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }

                        // 左侧导航项（仅文字）
                        NavigationItem.ExchangeRate.let { screen ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable {
                                        navController.navigate(screen.route) {
                                            // 避免重复添加相同的路线到返回栈
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = screen.title,
                                    fontSize = 16.sp, // 增大文字大小
                                    color = if (currentDestination?.destination?.route == screen.route) {
                                        MaterialTheme.colorScheme.primary // 使用主题主色调
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }

                        // 中间的添加按钮
                        FloatingActionButton(
                            onClick = {
                                navController.navigate(NavigationItem.AddTrip.route)
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .size(56.dp) // 标准FAB大小
                                .padding(vertical = 8.dp) // 添加上下间距
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "添加旅行"
                            )
                        }

                        // 右侧导航项（仅文字）
                        NavigationItem.Translation.let { screen ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable {
                                        navController.navigate(screen.route) {
                                            // 避免重复添加相同的路线到返回栈
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = screen.title,
                                    fontSize = 16.sp, // 增大文字大小
                                    color = if (currentDestination?.destination?.route == screen.route) {
                                        MaterialTheme.colorScheme.primary // 使用主题主色调
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }

                        // 右侧导航项（仅文字）
                        NavigationItem.Weather.let { screen ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable {
                                        navController.navigate(screen.route) {
                                            // 避免重复添加相同的路线到返回栈
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = screen.title,
                                    fontSize = 16.sp, // 增大文字大小
                                    color = if (currentDestination?.destination?.route == screen.route) {
                                        MaterialTheme.colorScheme.primary // 使用主题主色调
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = NavigationItem.Home.route
            ) {
                HomeScreenWithFab(
                    navigateToAddTrip = {
                        navController.navigate(NavigationItem.AddTrip.route)
                    },
                    navigateToTripDetail = { tripId ->
                        navController.navigate("${NavigationItem.TripDetail.route.replace("{tripId}", tripId.toString())}")
                    }
                )
            }

            composable(
                route = NavigationItem.AddTrip.route
            ) {
                AddTripScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onTripSaved = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = NavigationItem.TripDetail.route,
                arguments = tripDetailArguments
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getLong(NavigationArgs.TRIP_ID_ARG) ?: 0L
                TripDetailScreen(
                    tripId = tripId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToEdit = { tripId ->
                        navController.navigate("${NavigationItem.EditTrip.route.replace("{tripId}", tripId.toString())}")
                    },
                    onDeleteTrip = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = NavigationItem.EditTrip.route,
                arguments = editTripArguments
            ) { backStackEntry ->
                val tripId = backStackEntry.arguments?.getLong(NavigationArgs.TRIP_ID_ARG) ?: 0L
                EditTripScreen(
                    tripId = tripId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onTripSaved = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = NavigationItem.ExchangeRate.route
            ) {
                ExchangeRateScreen()
            }

            composable(
                route = NavigationItem.Translation.route
            ) {
                TranslationScreen()
            }

            composable(
                route = NavigationItem.Weather.route
            ) {
                WeatherScreen()
            }
        }
    }
}
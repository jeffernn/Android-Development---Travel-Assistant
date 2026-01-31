package com.example.traveljournal.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.traveljournal.R
import com.example.traveljournal.components.TripCard
import com.example.traveljournal.util.ThemeManager
import com.example.traveljournal.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenWithFab(
    navigateToAddTrip: () -> Unit,
    navigateToTripDetail: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HomeScreen(
            navigateToAddTrip = navigateToAddTrip,
            navigateToTripDetail = navigateToTripDetail,
            scrollBehavior = scrollBehavior,
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToAddTrip: () -> Unit,
    navigateToTripDetail: (Long) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val trips by viewModel.trips.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val themeManager = remember { ThemeManager(context) }
    val isDarkTheme by com.example.traveljournal.util.observeDarkTheme()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(searchQuery.text) {
        viewModel.setSearchQuery(searchQuery.text)
    }

    // 使用Scaffold创建更现代化的布局
    Scaffold(
        topBar = {
            // 搜索栏移到顶部 - 使用椭圆形搜索框
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 搜索框，宽度调整以适应新按钮
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp), // 增加高度
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "点击搜索具体旅游笔记..",
                                    fontSize = 14.sp
                                )
                            },
                            singleLine = true,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(50), // 椭圆形
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 20.sp // 调整行高
                            )
                        )

                        // 深色模式切换按钮
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    themeManager.setDarkTheme(!isDarkTheme)
                                }
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.WbSunny else Icons.Default.NightsStay,
                                contentDescription = if (isDarkTheme) "切换到浅色模式" else "切换到深色模式",
                                tint = if (isDarkTheme) androidx.compose.ui.graphics.Color.Yellow else androidx.compose.ui.graphics.Color(0xFF004469)
                            )
                        }
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                Spacer(modifier = Modifier.height(4.dp))

                // 移除"最近的旅行"标题

                // 加载状态
                if (uiState.loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "正在加载旅行记录...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else if (trips.isEmpty()) {
                    // 空状态 - 使用更吸引人的设计
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(bottom = 16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "还没有旅行记录",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "点击右下角的加号按钮开始记录您的第一次旅行",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    // 旅行列表 - 使用瀑布流布局
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2), // 两列瀑布流
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 1.5.dp),
                        contentPadding = PaddingValues(horizontal = 1.dp, vertical = 1.dp),
                        verticalItemSpacing = 1.dp, // 卡片间距 1dp
                        horizontalArrangement = Arrangement.spacedBy(1.dp) // 水平间距 1dp
                    ) {
                        items(trips) { trip ->
                            TripCard(
                                trip = trip,
                                onClick = { navigateToTripDetail(trip.id) }
                            )
                        }
                    }
                }
            }
        }
    )
}
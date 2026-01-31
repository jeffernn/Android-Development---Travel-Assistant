package com.example.traveljournal.ui.screens

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.traveljournal.components.LocationItem
import com.example.traveljournal.util.*
import com.example.traveljournal.viewmodel.AddEditTripViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTripScreen(
    tripId: Long,
    onNavigateBack: () -> Unit,
    onTripSaved: () -> Unit,
    viewModel: AddEditTripViewModel = hiltViewModel()
) {
    val trip by viewModel.trip.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var title by remember { mutableStateOf(trip.title) }
    var description by remember { mutableStateOf(trip.description) }
    var date by remember { mutableStateOf(trip.date) }
    // 日期选择器
    var showDatePicker by remember { mutableStateOf(false) }

    // 图片选择器
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.saveImageToInternalStorage(it)
        }
    }

    // 位置相关变量
    val context = LocalContext.current
    val fusedLocationProviderClient = rememberLocationProvider()
    val locationPermissionLauncher = rememberLocationPermissionLauncher { isGranted ->
        if (isGranted) {
            // 设置一个超时机制，防止定位一直显示"定位中"
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                if (viewModel.uiState.value.locationLoading) {
                    // 如果超过10秒还在加载，就停止加载并提示用户
                    viewModel.updateUiMessage("定位超时，请检查GPS是否开启或稍后重试")
                    viewModel.updateLocationLoading(false)
                }
            }, 10000) // 10秒超时

            // 使用系统定位服务获取位置
            val location = getLocation(context)
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val placeName = getPlaceNameFromLocation(context, latitude, longitude)
                viewModel.addCurrentLocation(placeName, latitude, longitude)
            } else {
                viewModel.updateUiMessage("无法获取当前位置，请稍后重试")
                viewModel.updateLocationLoading(false)
            }
        } else {
            // 权限被拒绝，显示提示
            viewModel.updateUiMessage("需要位置权限来获取当前位置")
            // 停止加载状态
            viewModel.updateLocationLoading(false)
        }
    }

    LaunchedEffect(trip) {
        title = trip.title
        description = trip.description
        date = trip.date
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("确定", fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("取消", fontWeight = FontWeight.Medium)
                }
            }
        ) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = if (date.isNotEmpty()) {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)?.time
                } else {
                    System.currentTimeMillis()
                }?.let { it } ?: System.currentTimeMillis()
            )
            DatePicker(state = datePickerState)
            LaunchedEffect(datePickerState.selectedDateMillis) {
                if (datePickerState.selectedDateMillis != null) {
                    date = datePickerState.selectedDateMillis?.let {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                    } ?: ""
                    viewModel.updateDate(date)
                }
            }
        }
    }

    // 显示加载状态
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
                Spacer(modifier = Modifier.height(16.dp))
                uiState.message?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // 显示错误信息
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // 可以显示一个snackbar或其他错误提示
        }
    }

    // 显示成功信息
    if (uiState.success) {
        LaunchedEffect(uiState.success) {
            onTripSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "编辑旅行",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    // 保存按钮移动到右上角
                    IconButton(
                        onClick = { viewModel.saveTrip() },
                        enabled = title.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "保存",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 标题输入
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    viewModel.updateTitle(it)
                },
                label = { Text("旅行标题") },
                placeholder = { Text("例如：北京之旅") },
                modifier = Modifier
                    .fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 描述输入
            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    viewModel.updateDescription(it)
                },
                label = { Text("旅行描述") },
                placeholder = { Text("记录您的旅行经历...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 日期选择
            OutlinedTextField(
                value = date,
                onValueChange = { },
                readOnly = true,
                label = { Text("旅行日期") },
                placeholder = { Text("点击选择日期") },
                modifier = Modifier
                    .fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "选择日期",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 图片区域标题
            Text(
                text = "旅行照片",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 图片选择和显示
            trip.photoPath?.let { photoPath ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = if (photoPath.startsWith("content://") || photoPath.startsWith("http")) {
                                photoPath
                            } else {
                                // 如果是文件路径，使用File对象
                                java.io.File(photoPath)
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { imageLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "选择照片",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 位置列表标题
            Text(
                text = "旅游定位",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            locations.forEach { location ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LocationItem(location = location)
                    IconButton(
                        onClick = { viewModel.removeLocation(location) },
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除位置",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 按钮布局
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.locationLoading) {
                    // 显示加载状态的按钮
                    OutlinedButton(
                        onClick = { /* 禁用点击 */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        enabled = false,  // 禁用按钮
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "定位中...",
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            // 检查是否已经添加了位置，如果已添加则不重复添加
                            if (locations.isEmpty()) {
                                // 先更新UI状态为加载中
                                viewModel.startLocationRetrieval()
                                // 检查位置权限
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            } else {
                                viewModel.updateUiMessage("已经添加了位置，无法重复添加")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "添加旅游定位",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

        }
    }
}

// 使用系统定位服务获取位置
private fun getLocation(context: Context): Location? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    // 首先尝试从GPS获取位置
    if (isGpsEnabled) {
        val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (gpsLocation != null) {
            return gpsLocation
        }
    }

    // 如果GPS不可用或没有位置，尝试网络定位
    if (isNetworkEnabled) {
        val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (networkLocation != null) {
            return networkLocation
        }
    }

    // 如果以上都失败，返回null
    return null
}
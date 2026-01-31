package com.example.traveljournal.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.traveljournal.domain.model.Trip

@Composable
fun TripCard(
    trip: Trip,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp) // 减少内边距以适应新设计
        ) {
            // 图片区域 - 固定宽高比，直角
            trip.photoPath?.let { photoPath ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f), // 固定宽高比
                        contentScale = ContentScale.Crop // 图片自适应裁剪
                    )
                }
            }

            // 标题文本
            Text(
                text = trip.title,
                fontSize = 14.sp, // 14sp
                fontWeight = FontWeight.Normal,
                maxLines = 2, // 最多2行
                overflow = TextOverflow.Ellipsis, // 超出显示省略号
                color = MaterialTheme.colorScheme.onSurface, // 使用主题表面文字颜色
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            )

            // 根据ID添加额外空白区域以实现不同高度 - ID为偶数的卡片更长
            if (trip.id % 2L == 0L) {
                // 添加一个额外的空白区域来增加左侧卡片的高度
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 发布时间文本
            Text(
                text = trip.date,
                fontSize = 12.sp, // 12sp
                color = MaterialTheme.colorScheme.onSurfaceVariant, // 使用主题变体表面文字颜色
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
            )
        }
    }
}
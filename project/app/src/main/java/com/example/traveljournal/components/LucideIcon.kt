package com.example.traveljournal.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest

/**
 * Lucide图标组件，用于显示Lucide图标库中的图标
 * 支持Vector和SVG格式的图标
 */
@Composable
fun LucideIcon(
    icon: LucideIconType,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = LocalContentColor.current
) {
    when (icon) {
        is LucideIconType.Vector -> {
            Image(
                painter = rememberVectorPainter(icon.imageVector),
                contentDescription = icon.contentDescription,
                modifier = modifier.size(size),
                colorFilter = ColorFilter.tint(tint)
            )
        }
        is LucideIconType.Svg -> {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(icon.svgData)
                        .decoderFactory(SvgDecoder.Factory())
                        .build()
                ),
                contentDescription = icon.contentDescription,
                modifier = modifier.size(size),
                colorFilter = ColorFilter.tint(tint)
            )
        }
    }
}

/**
 * Lucide图标类型密封类
 */
sealed class LucideIconType {
    abstract val contentDescription: String?

    data class Vector(
        val imageVector: ImageVector,
        override val contentDescription: String? = null
    ) : LucideIconType()

    data class Svg(
        val svgData: String, // SVG字符串
        override val contentDescription: String? = null
    ) : LucideIconType()
}

/**
 * 预定义的Lucide图标
 */
object LucideIcons {
    val CircleX = LucideIconType.Svg(
        svgData = """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-circle-x-icon lucide-circle-x"><circle cx="12" cy="12" r="10"/><path d="m15 9-6 6"/><path d="m9 9 6 6"/></svg>""",
        contentDescription = "Circle X"
    )

    // 可以在这里添加更多Lucide图标
    val Home = LucideIconType.Svg(
        svgData = """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><path d="M9 22V12h6v10"/></svg>""",
        contentDescription = "Home"
    )

    val Search = LucideIconType.Svg(
        svgData = """<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>""",
        contentDescription = "Search"
    )
}
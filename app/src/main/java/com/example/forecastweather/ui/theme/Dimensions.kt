package com.example.forecastweather.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1️⃣ Định nghĩa enum ScreenType
enum class ScreenType {
    Small, Medium, Large
}

// 2️⃣ Hàm detect screen type
@Composable
fun getScreenType(): ScreenType {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    return when {
        screenWidthDp < 360 -> ScreenType.Small
        screenWidthDp < 600 -> ScreenType.Medium
        else -> ScreenType.Large
    }
}

// 3️⃣ Class Dimensions lưu spacing & size
data class Dimensions(
    val spacingSmall: Dp,
    val spacingMedium: Dp,
    val spacingLarge: Dp,
    val iconSize: Dp,
    val cardHeight: Dp,
    val fontSizeLarge: TextUnit
)

// 4️⃣ Hàm cung cấp kích thước theo screenType
@Composable
fun provideDimensions(screenType: ScreenType): Dimensions {
    return when (screenType) {
        ScreenType.Small -> Dimensions(8.dp, 12.dp, 16.dp, 24.dp, 160.dp, 18.sp)
        ScreenType.Medium -> Dimensions(12.dp, 16.dp, 20.dp, 32.dp, 180.dp, 20.sp)
        ScreenType.Large -> Dimensions(16.dp, 20.dp, 24.dp, 40.dp, 200.dp, 22.sp)
    }
}

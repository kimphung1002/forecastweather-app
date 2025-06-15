package com.example.forecastweather.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.forecastweather.domain.mapper.WeatherIconMapper

@Composable
fun WeatherIcon(
    iconCode: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fileName = WeatherIconMapper.mapToLocalFileName(iconCode)

    val resId = context.resources.getIdentifier(fileName, "raw", context.packageName)
    val imageLoader = ImageLoader.Builder(context)
        .components { add(SvgDecoder.Factory()) }
        .build()

    val data = "android.resource://${context.packageName}/raw/$fileName"

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(data)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        imageLoader = imageLoader,
        contentDescription = null,
        modifier = modifier
    )
}

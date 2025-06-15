// WeeklyForecastScreen.kt

package com.example.forecastweather.ui.weekly

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.forecastweather.data.api.NetworkResponse
import com.example.forecastweather.domain.model.DailyForecast
import com.example.forecastweather.domain.model.UserSettings
import com.example.forecastweather.ui.settings.SettingsViewModel
import com.example.forecastweather.ui.util.WeatherIcon
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LocationInfoRow(city: String, country: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$city, $country",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyForecastScreen(
    viewModel: WeeklyForecastViewModel = viewModel(),
    settingsViewModel: SettingsViewModel,
    city: String
) {
    val forecastState by viewModel.forecast.observeAsState()
    val settings by settingsViewModel.userSettings.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadWeeklyForecast(city)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dự báo 5 ngày tiếp theo",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            when (val result = forecastState) {
                is NetworkResponse.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                is NetworkResponse.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Lỗi: ${result.message}", color = MaterialTheme.colorScheme.error)
                }
                is NetworkResponse.Success -> {
                    // ✅ Lấy city & country từ API trả về:
                    LocationInfoRow(
                        city = result.data.cityName,
                        country = result.data.countryCode
                    )

                    WeeklyForecastList(
                        forecasts = result.data.dailyForecasts,
                        settings = settings
                    )
                }
                null -> {}
            }
        }
    }
}


@Composable
fun ForecastDayItem(forecast: DailyForecast, settings: UserSettings?) {
    var expanded by remember { mutableStateOf(false) }
    val tempUnit = when (settings?.temperatureUnit) { "°F", "imperial" -> "imperial"; else -> "metric" }
    val windUnit = settings?.windUnit ?: "km/h"
    val is24Hour = settings?.timeFormat == "24h"

    val tempMax = convertTemp(forecast.tempMax, tempUnit)
    val tempMin = convertTemp(forecast.tempMin, tempUnit)
    val wind = convertWind(forecast.wind, windUnit)
    val sunriseFormatted = formatTime(forecast.sunrise, is24Hour)
    val sunsetFormatted = formatTime(forecast.sunset, is24Hour)

    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val cardBackgroundColor = if (isDark) Color(0xFFEEEEEE) else MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(2.5f)) {
                    Text(
                        text = forecast.date,
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = forecast.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                WeatherIcon(
                    iconCode = forecast.icon,
                    modifier = Modifier
                        .size(52.dp)
                        .padding(horizontal = 4.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = tempMax,
                        style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = tempMin,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            if (expanded) {
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                ForecastDetailItem(label = "Gió", value = "$wind $windUnit")
                ForecastDetailItem(label = "Độ ẩm", value = "${forecast.humidity}%")
                ForecastDetailItem(label = "Mặt trời mọc - lặn", value = "$sunriseFormatted - $sunsetFormatted")
            }
        }
    }
}

@Composable
fun WeeklyForecastList(forecasts: List<DailyForecast>, settings: UserSettings?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(forecasts) { forecastItem -> ForecastDayItem(forecastItem, settings) }
    }
}

@Composable
fun ForecastDetailItem(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ================== Logic xử lý đơn vị và format ===================

fun convertTemp(tempC: Int, unit: String): String {
    return if (unit == "imperial") {
        val tempF = tempC * 9 / 5 + 32
        "$tempF°F"
    } else {
        "$tempC°C"
    }
}


fun convertWind(windKmh: Double, unit: String): String {
    return if (unit == "m/s") {
        val windMs = windKmh / 3.6
        String.format("%.1f", windMs)
    } else {
        String.format("%.1f", windKmh)
    }
}

fun formatTime(time: String, is24Hour: Boolean): String {
    return if (is24Hour) time else {
        try {
            val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
            val sdf12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = sdf24.parse(time)
            sdf12.format(date!!)
        } catch (e: Exception) {
            time
        }
    }
}
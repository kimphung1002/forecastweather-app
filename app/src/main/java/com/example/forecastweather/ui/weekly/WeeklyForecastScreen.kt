package com.example.forecastweather.ui.weekly

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.forecastweather.data.api.NetworkResponse
import com.example.forecastweather.domain.model.DailyForecast
import com.example.forecastweather.domain.model.UserSettings
import com.example.forecastweather.ui.settings.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Locale

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
                    Column {
                        Text(
                            text = "Dự báo thời tiết 5 ngày tiếp theo",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = Color(0xFF7C4DFF),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = city,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF7C4DFF)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val result = forecastState) {
                is NetworkResponse.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is NetworkResponse.Error -> Text(
                    text = "Lỗi: ${result.message}",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
                is NetworkResponse.Success -> WeeklyForecastList(result.data, settings)
                null -> {}
            }
        }
    }
}

@Composable
fun WeeklyForecastList(forecasts: List<DailyForecast>, settings: UserSettings?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        items(forecasts) { forecastItem ->
            ForecastDayItem(forecast = forecastItem, settings)
        }
    }
}

@Composable
fun ForecastDayItem(forecast: DailyForecast, settings: UserSettings?) {
    var expanded by remember { mutableStateOf(false) }

    // Map dữ liệu trong UserSettings thành giá trị dùng trong UI
    val tempUnit = when (settings?.temperatureUnit) {
        "°F", "imperial" -> "imperial"
        else -> "metric" // mặc định °C
    }

    val windUnit = settings?.windUnit ?: "km/h"

    val is24Hour = settings?.timeFormat == "24h"

    val tempMax = convertTemp(forecast.tempMax, tempUnit)
    val tempMin = convertTemp(forecast.tempMin, tempUnit)
    val wind = convertWind(forecast.wind, windUnit)
    val sunriseFormatted = formatTime(forecast.sunrise, is24Hour)
    val sunsetFormatted = formatTime(forecast.sunset, is24Hour)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FF)),
        border = BorderStroke(1.dp, Color(0xFFE0E0F0)),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = forecast.date,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = forecast.description,
                        color = Color(0xFF7C4DFF),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Image(
                    painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${forecast.icon}@2x.png"),
                    contentDescription = null,
                    modifier = Modifier.size(70.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = tempMax,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = tempMin,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF7C4DFF)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            if (expanded) {
                Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFE0E0F0))

                ForecastDetailItem(label = "Wind", value = "$wind $windUnit")
                ForecastDetailItem(label = "Humidity", value = "${forecast.humidity}%")
                ForecastDetailItem(label = "Sunset - Sunrise", value = "$sunriseFormatted - $sunsetFormatted")
            }
        }
    }
}

@Composable
fun ForecastDetailItem(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF7C4DFF),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// Các hàm xử lý logic đơn vị

fun convertTemp(tempC: Double, unit: String): String {
    return if (unit == "imperial") {
        val tempF = tempC * 9 / 5 + 32
        "${String.format("%.1f", tempF)}°F"
    } else {
        "${String.format("%.1f", tempC)}°C"
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

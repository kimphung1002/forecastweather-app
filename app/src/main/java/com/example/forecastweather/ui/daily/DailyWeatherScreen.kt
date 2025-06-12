package com.example.forecastweather.ui.daily

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.forecastweather.data.api.NetworkResponse
import com.example.forecastweather.data.api.WeatherModel
import com.example.forecastweather.data.model.ForecastItem
import com.example.forecastweather.data.model.ForecastResponse
import com.example.forecastweather.ui.settings.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun DailyWeatherScreen(
    viewModel: DailyWeatherViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToSearch: () -> Unit
) {
    val weatherResult = viewModel.weatherResult.observeAsState()
    val userSettings by settingsViewModel.userSettings.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.reloadIfNeeded()
    }

    when (val result = weatherResult.value) {
        is NetworkResponse.Loading, null -> LoadingState()
        is NetworkResponse.Error -> ErrorState(result.message)
        is NetworkResponse.Success -> SuccessState(
            data = result.data,
            forecastData = viewModel.forecastResult.value,
            userSettings = userSettings
        )
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = Color.Red)
    }
}

@Composable
fun SuccessState(
    data: WeatherModel,
    forecastData: NetworkResponse<ForecastResponse>?,
    userSettings: com.example.forecastweather.domain.model.UserSettings
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocationInfo(data)
        Spacer(modifier = Modifier.height(16.dp))

        val (maxTemp, minTemp) = if (forecastData is NetworkResponse.Success) {
            calculateTodayMaxMinTemp(forecastData.data.list, data.timezone)
        } else {
            Pair(data.main.tempMax, data.main.tempMin)
        }

        WeatherMainInfo(data, maxTemp, minTemp, userSettings)
        Spacer(modifier = Modifier.height(24.dp))

        WeatherInfoCard(
            humidity = data.main.humidity,
            windSpeed = data.wind.speed,
            sunrise = data.sys.sunrise,
            sunset = data.sys.sunset,
            timezoneOffset = data.timezone,
            userSettings = userSettings
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (forecastData is NetworkResponse.Success) {
            HourlyForecast(forecastData.data.list.take(8), data.timezone, userSettings)
        }
    }
}

@Composable
fun LocationInfo(data: WeatherModel) {
    Text(
        text = "${data.name}, ${data.sys.country}",
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun WeatherMainInfo(
    data: WeatherModel,
    maxTemp: Double,
    minTemp: Double,
    userSettings: com.example.forecastweather.domain.model.UserSettings
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${data.weather.firstOrNull()?.icon}@4x.png",
                contentDescription = "Weather Icon",
                modifier = Modifier.size(220.dp)
            )
            Text(
                text = data.weather.firstOrNull()?.description.orEmpty().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Hôm nay: ${SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(Date(System.currentTimeMillis()))}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTemperature(data.main.temp, userSettings.temperatureUnit),
                style = MaterialTheme.typography.displayMedium.copy(color = Color(0xFF0D47A1))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "C: ${formatTemperature(maxTemp, userSettings.temperatureUnit)} | T: ${formatTemperature(minTemp, userSettings.temperatureUnit)}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF0D47A1),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun WeatherInfoCard(
    humidity: Int,
    windSpeed: Double,
    sunrise: Long,
    sunset: Long,
    timezoneOffset: Int,
    userSettings: com.example.forecastweather.domain.model.UserSettings
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherInfoItem(Icons.Default.WaterDrop, "Độ ẩm", "$humidity %")
                WeatherInfoItem(Icons.Default.WindPower, "Gió", formatWind(windSpeed, userSettings.windUnit))
                WeatherInfoItem(Icons.Default.WbSunny, "Mặt trời mọc", formatTime(sunrise, timezoneOffset, userSettings.timeFormat))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Mặt trời lặn: ${formatTime(sunset, timezoneOffset, userSettings.timeFormat)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun WeatherInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun HourlyForecast(forecastList: List<ForecastItem>, timezone: Int, userSettings: com.example.forecastweather.domain.model.UserSettings) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(forecastList) { item ->
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .width(140.dp)
                    .height(220.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(item.dt, timezone, userSettings.timeFormat),
                        style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF1565C0), fontWeight = FontWeight.SemiBold)
                    )
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${item.weather[0].icon}@4x.png",
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(120.dp)
                    )
                    Text(
                        text = formatTemperature(item.main.temp, userSettings.temperatureUnit),
                        style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF0D47A1), fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

fun calculateTodayMaxMinTemp(forecastList: List<ForecastItem>, timezoneOffset: Int): Pair<Double, Double> {
    val now = System.currentTimeMillis() / 1000
    val localNow = now + timezoneOffset
    val todayEpochDay = localNow / 86400

    val todayForecasts = forecastList.filter {
        val localForecastTime = it.dt + timezoneOffset
        val forecastDay = localForecastTime / 86400
        forecastDay == todayEpochDay
    }

    val maxTemp = todayForecasts.maxOfOrNull { it.main.tempMax } ?: 0.0
    val minTemp = todayForecasts.minOfOrNull { it.main.tempMin } ?: 0.0
    return Pair(maxTemp, minTemp)
}

fun formatTemperature(temp: Double, unit: String): String {
    return if (unit == "°F") {
        val fahrenheit = temp * 9 / 5 + 32
        "${fahrenheit.toInt()}°F"
    } else {
        "${temp.toInt()}°C"
    }
}

fun formatWind(speed: Double, unit: String): String {
    return when (unit) {
        "m/s" -> "$speed m/s"
        "mph" -> String.format("%.1f mph", speed * 2.23694)
        else -> "$speed km/h"
    }
}

fun formatTime(timestamp: Long, timezoneOffset: Int, timeFormat: String): String {
    val date = Date((timestamp + timezoneOffset) * 1000)
    val pattern = if (timeFormat == "12h") "hh:mm a" else "HH:mm"
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(date)
}

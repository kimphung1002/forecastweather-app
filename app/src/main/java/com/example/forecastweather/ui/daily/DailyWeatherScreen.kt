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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.forecastweather.data.api.NetworkResponse
import com.example.forecastweather.data.api.WeatherModel
import com.example.forecastweather.data.model.ForecastItem
import com.example.forecastweather.data.model.ForecastResponse
import com.example.forecastweather.domain.model.UserSettings
import com.example.forecastweather.ui.settings.SettingsViewModel
import com.example.forecastweather.ui.util.WeatherIcon
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

    LaunchedEffect(userSettings) {
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
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun SuccessState(
    data: WeatherModel,
    forecastData: NetworkResponse<ForecastResponse>?,
    userSettings: UserSettings
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val iconSize = (screenWidth * 0.35f).coerceIn(100.dp, 140.dp)
    val cardHeight = (screenHeight * 0.18f).coerceIn(100.dp, 130.dp)
    val cardWidth = (screenWidth * 0.28f).coerceIn(80.dp, 110.dp)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            LocationInfo(data)
            Spacer(modifier = Modifier.height(8.dp))
        }

        val (maxTemp, minTemp) = if (forecastData is NetworkResponse.Success) {
            calculateTodayMaxMinTemp(forecastData.data.list, data.timezone)
        } else Pair(data.main.tempMax, data.main.tempMin)

        item {
            WeatherMainInfo(
                data = data,
                maxTemp = maxTemp,
                minTemp = minTemp,
                userSettings = userSettings,
                iconSize = iconSize
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            WeatherInfoCard(
                humidity = data.main.humidity,
                windSpeed = data.wind.speed,
                sunrise = data.sys.sunrise,
                sunset = data.sys.sunset,
                timezoneOffset = data.timezone,
                userSettings = userSettings
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (forecastData is NetworkResponse.Success) {
            item {
                HourlyForecast(
                    forecastList = forecastData.data.list.take(8),
                    timezone = data.timezone,
                    userSettings = userSettings,
                    cardHeight = cardHeight,
                    cardWidth = cardWidth
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun LocationInfo(data: WeatherModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "${data.name}, ${data.sys.country}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun WeatherMainInfo(
    data: WeatherModel,
    maxTemp: Double,
    minTemp: Double,
    userSettings: UserSettings,
    iconSize: Dp
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatTemperature(data.main.temp, userSettings.temperatureUnit),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(2.dp))

        WeatherIcon(
            iconCode = data.weather.firstOrNull()?.icon.orEmpty(),
            modifier = Modifier.size(iconSize)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = data.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }.orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "C: ${formatTemperature(maxTemp, userSettings.temperatureUnit)} - T: ${formatTemperature(minTemp, userSettings.temperatureUnit)}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WeatherInfoCard(
    humidity: Int,
    windSpeed: Double,
    sunrise: Long,
    sunset: Long,
    timezoneOffset: Int,
    userSettings: UserSettings
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WeatherInfoItem(
                    icon = Icons.Default.WaterDrop,
                    label = "Độ ẩm",
                    value = "$humidity %",
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                )
                WeatherInfoItem(
                    icon = Icons.Default.WindPower,
                    label = "Gió",
                    value = formatWind(windSpeed, userSettings.windUnit),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                )
                WeatherInfoItem(
                    icon = Icons.Default.WbSunny,
                    label = "Bình minh",
                    value = formatTime(sunrise, timezoneOffset, userSettings.timeFormat),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatCurrentDateTime(userSettings.timeFormat),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center

            )
        }
    }
}

@Composable
fun WeatherInfoItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HourlyForecast(
    forecastList: List<ForecastItem>,
    timezone: Int,
    userSettings: UserSettings,
    cardHeight: Dp,
    cardWidth: Dp
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(forecastList) { item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(3.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .width(cardWidth)
                    .height(cardHeight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = formatTime(item.dt, timezone, userSettings.timeFormat),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    WeatherIcon(
                        iconCode = item.weather[0].icon,
                        modifier = Modifier.size(cardWidth * 0.5f)
                    )
                    Text(
                        text = formatTemperature(item.main.temp, userSettings.temperatureUnit),
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
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

fun getTemperatureSymbol(unit: String): String {
    return if (unit == "imperial") "°F" else "°C"
}
fun formatTemperature(temp: Double, unit: String): String {
    val value = if (unit == "imperial") {
        temp * 9 / 5 + 32
    } else {
        temp
    }
    return "${value.toInt()}${getTemperatureSymbol(unit)}"
}




fun formatCurrentDateTime(timeFormat: String): String {
    val currentTime = System.currentTimeMillis()
    val date = Date(currentTime)
    val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("vi"))
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi"))
    val timePattern = if (timeFormat == "12h") "hh:mm a" else "HH:mm"
    val timeFormatSdf = SimpleDateFormat(timePattern, Locale.getDefault())

    val dayOfWeek = dayOfWeekFormat.format(date).replaceFirstChar { it.uppercase() }
    val dateStr = dateFormat.format(date)
    val timeStr = timeFormatSdf.format(date)
    return "$dayOfWeek, $dateStr - $timeStr"
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
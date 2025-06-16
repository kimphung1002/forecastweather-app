package com.example.forecastweather.domain.mapper

import com.example.forecastweather.data.model.ForecastResponse
import com.example.forecastweather.domain.model.DailyForecast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object ForecastMapper {

    fun mapToDailyForecast(
        forecastResponse: ForecastResponse,
        sunrise: Long,
        sunset: Long
    ): List<DailyForecast> {

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
        }
        val sdfDisplay = SimpleDateFormat("EEEE d/M", Locale("vi"))  // ✅ chỉnh đúng format yêu cầu

        val grouped = forecastResponse.list.groupBy {
            sdf.format(Date(it.dt * 1000))
        }

        val today = sdf.format(Date())
        val filtered = grouped.filterKeys { it != today }.toSortedMap()

        return filtered.entries.take(5).map { entry ->
            val dailyList = entry.value

            val maxTemp = dailyList.maxOfOrNull { it.main.tempMax }?.toInt() ?: 0
            val minTemp = dailyList.minOfOrNull { it.main.tempMin }?.toInt() ?: 0

            // ✅ TIM ĐIỂM GẦN 12H NHẤT
            val noonTimestamp = getNoonTimestamp(entry.key)
            val closestItem = dailyList.minByOrNull { kotlin.math.abs(it.dt * 1000 - noonTimestamp) } ?: dailyList[0]

            val firstDesc = closestItem.weather[0]

            val calendar = Calendar.getInstance()
            calendar.time = sdf.parse(entry.key)!!

            DailyForecast(
                date = sdfDisplay.format(calendar.time).replaceFirstChar { it.uppercaseChar() },
                description = firstDesc.description.replaceFirstChar { it.uppercaseChar() },
                icon = normalizeIcon(firstDesc.icon),
                tempMax = maxTemp,
                tempMin = minTemp,
                wind = dailyList[0].wind.speed,
                humidity = dailyList[0].main.humidity,
                sunrise = SimpleDateFormat("HH:mm").format(Date(sunrise * 1000)),
                sunset = SimpleDateFormat("HH:mm").format(Date(sunset * 1000))
            )
        }
    }

    // ✅ Tính timestamp của 12h trưa
    private fun getNoonTimestamp(dateStr: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr) ?: Date()
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    // ✅ Chuẩn hóa icon (có thể thêm nếu muốn loại bỏ n thành d khi ban ngày)
    private fun normalizeIcon(icon: String): String {
        return if (icon.endsWith("n")) icon.replace("n", "d") else icon
    }
}

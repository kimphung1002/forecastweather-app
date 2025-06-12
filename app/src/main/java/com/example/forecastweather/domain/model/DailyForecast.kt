package com.example.forecastweather.domain.model

data class DailyForecast(
    val date: String,           // Thứ, ngày (vd: "Thứ 3, 10")
    val description: String,    // Trạng thái thời tiết ngắn gọn
    val icon: String,           // Mã icon
    val tempMax: Double,        // Nhiệt độ cao nhất
    val tempMin: Double,        // Nhiệt độ thấp nhất
    val wind: Double,           // Gió
    val humidity: Int,          // Độ ẩm
    val sunrise: String,        // Giờ mặt trời mọc
    val sunset: String          // Giờ mặt trời lặn
)

package com.example.forecastweather.domain.model

data class DailyForecast(
    val date: String,           // ngày (vd: "14/06")
    val description: String,    // Trạng thái thời tiết ngắn gọn
    val icon: String,           // Mã icon
    val tempMax: Int,           // Nhiệt độ cao nhất
    val tempMin: Int,           // Nhiệt độ thấp nhất
    val wind: Double,           // Gió (giữ nguyên vì có thể có số lẻ)
    val humidity: Int,          // Độ ẩm
    val sunrise: String,        // Giờ mặt trời mọc
    val sunset: String          // Giờ mặt trời lặn
)

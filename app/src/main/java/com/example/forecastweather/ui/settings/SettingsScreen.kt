package com.example.forecastweather.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(settingsViewModel: SettingsViewModel) {
    val settings by settingsViewModel.userSettings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cài đặt",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
        }
    ) { paddingValues ->
        val scrollState  = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState), // ✅ THÊM DÒNG NÀY
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingItem(
                icon = Icons.Filled.Thermostat,
                title = "Đơn vị nhiệt độ",
                subtitle = "°C hoặc °F"
            ) {
                DropdownSelector(
                    options = listOf("°C", "°F"),
                    selectedOption = if (settings.temperatureUnit == "metric") "°C" else "°F",
                    onOptionSelected = {
                        settingsViewModel.updateTemperatureUnit(if (it == "°C") "metric" else "imperial")
                    }
                )
            }

            SettingItem(
                icon = Icons.Outlined.Air,
                title = "Đơn vị gió",
                subtitle = "km/h, m/s, mph"
            ) {
                DropdownSelector(
                    options = listOf("km/h", "m/s", "mph"),
                    selectedOption = settings.windUnit,
                    onOptionSelected = { settingsViewModel.updateWindSpeedUnit(it) }
                )
            }

            SettingItem(
                icon = Icons.Filled.Timelapse,
                title = "Định dạng thời gian",
                subtitle = "12h hoặc 24h"
            ) {
                DropdownSelector(
                    options = listOf("12h", "24h"),
                    selectedOption = settings.timeFormat,
                    onOptionSelected = { settingsViewModel.updateTimeFormat(it) }
                )
            }

            SettingItem(
                icon = Icons.Outlined.DarkMode,
                title = "Chế độ giao diện",
                subtitle = "Sáng, Tối"
            ) {
                DropdownSelector(
                    options = listOf("Sáng", "Tối"),
                    selectedOption = settings.theme,
                    onOptionSelected = { settingsViewModel.updateTheme(it) }
                )
            }
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(42.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(12.dp)) // Khoảng cách trước dropdown

        trailingContent() // ✅ Đặt trong Row, không bị tràn
    }
}


@Composable
fun DropdownSelector(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Surface(
            modifier = Modifier
                .clickable { expanded = true }
                .height(42.dp)
                .width(100.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(selectedOption, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                Icon(Icons.Default.ArrowDropDown, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                val isSelected = option == selectedOption

                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    modifier = Modifier
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surface
                        )
                        .fillMaxWidth()
                )
            }
        }
    }
}



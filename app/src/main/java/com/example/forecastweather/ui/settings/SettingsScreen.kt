package com.example.forecastweather.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Cài đặt", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            SettingItem(
                icon = Icons.Filled.Thermostat,
                title = "Đơn vị nhiệt độ",
                subtitle = "°C hoặc °F",
                trailingContent = {
                    DropdownSelector(
                        options = listOf("°C", "°F"),
                        selectedOption = if (settings.temperatureUnit == "metric") "°C" else "°F",
                        onOptionSelected = { selected ->
                            val unit = if (selected == "°C") "metric" else "imperial"
                            settingsViewModel.updateTemperatureUnit(unit)
                        }
                    )
                }
            )

            SettingItem(
                icon = Icons.Outlined.Air,
                title = "Đơn vị gió",
                subtitle = "km/h, m/s, mph",
                trailingContent = {
                    DropdownSelector(
                        options = listOf("km/h", "m/s", "mph"),
                        selectedOption = settings.windUnit,
                        onOptionSelected = { settingsViewModel.updateWindSpeedUnit(it) }
                    )
                }
            )

            SettingItem(
                icon = Icons.Filled.Timelapse,
                title = "Định dạng thời gian",
                subtitle = "12h hoặc 24h",
                trailingContent = {
                    DropdownSelector(
                        options = listOf("12h", "24h"),
                        selectedOption = settings.timeFormat,
                        onOptionSelected = { settingsViewModel.updateTimeFormat(it) }
                    )
                }
            )



            SettingItem(
                icon = Icons.Outlined.DarkMode,
                title = "Chế độ giao diện",
                subtitle = "Sáng, Tối",
                trailingContent = {
                    DropdownSelector(
                        options = listOf("Sáng", "Tối"),
                        selectedOption = settings.theme,
                        onOptionSelected = { settingsViewModel.updateTheme(it) }
                    )
                }
            )
        }
    }
}

@Composable
fun SettingItem(icon: ImageVector, title: String, subtitle: String, trailingContent: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        trailingContent()
    }
}

@Composable
fun DropdownSelector(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier.clickable { expanded = true }.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selectedOption, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown Arrow", tint = MaterialTheme.colorScheme.primary)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(option, fontWeight = if (option == selectedOption) FontWeight.Bold else FontWeight.Normal,
                            color = if (option == selectedOption) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    modifier = if (option == selectedOption) Modifier.background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)) else Modifier
                )
            }
        }
    }
}

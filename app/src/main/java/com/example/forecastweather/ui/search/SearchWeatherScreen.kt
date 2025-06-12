package com.example.forecastweather.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.forecastweather.data.api.NetworkResponse
import com.example.forecastweather.ui.daily.DailyWeatherViewModel
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchWeatherScreen(
    viewModel: DailyWeatherViewModel,
    searchViewModel: SearchViewModel,
    onBack: () -> Unit
) {
    val weatherResult by viewModel.weatherResult.observeAsState()
    val history by searchViewModel.history.collectAsState()

    var city by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasSearched by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val suggestions = history.filter { it.contains(city, ignoreCase = true) && city.isNotBlank() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tìm kiếm thời tiết") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Ô nhập
            Column {
                OutlinedTextField(
                    value = city,
                    onValueChange = {
                        city = it
                        errorMessage = null
                        expanded = true
                    },
                    label = { Text("Nhập tên thành phố") },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = {
                        errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        searchCity(city, viewModel, searchViewModel, { hasSearched = true }, { errorMessage = it })
                        expanded = false
                    }),
                    trailingIcon = {
                        if (city.isNotEmpty()) {
                            IconButton(onClick = {
                                city = ""
                                expanded = false
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Xoá nội dung")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Gợi ý gần ô nhập
                androidx.compose.material3.DropdownMenu(
                    expanded = expanded && suggestions.isNotEmpty(),
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    suggestions.forEach { suggestion ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(suggestion) },
                            onClick = {
                                city = suggestion
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lịch sử + Nút xóa nằm cùng hàng
            if (history.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Lịch sử tìm kiếm", modifier = Modifier.weight(1f))
                    TextButton(onClick = { searchViewModel.clearAll() }) {
                        Text("Xoá tất cả")
                    }
                }

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    history.forEach { item ->
                        AssistChip(
                            onClick = { city = item },
                            label = { Text(item, maxLines = 1) },
                            trailingIcon = {
                                IconButton(onClick = { searchViewModel.removeCity(item) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Xoá")
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

//            // Lỗi nếu có
//            errorMessage?.let {
//                Text(it, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
//            }

            // Progress khi loading
            if (weatherResult is NetworkResponse.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Nút tìm kiếm nằm dưới
            Button(
                onClick = {
                    searchCity(city, viewModel, searchViewModel, { hasSearched = true }, { errorMessage = it })
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tìm kiếm")
            }

            // Xử lý kết quả trả về
            LaunchedEffect(weatherResult, hasSearched) {
                if (hasSearched) {
                    when (weatherResult) {
                        is NetworkResponse.Success -> onBack()
                        is NetworkResponse.Error -> errorMessage = (weatherResult as NetworkResponse.Error).message
                        else -> {}
                    }
                }
            }
        }
    }
}

private fun searchCity(
    city: String,
    viewModel: DailyWeatherViewModel,
    searchViewModel: SearchViewModel,
    onSearchStarted: () -> Unit,
    onError: (String) -> Unit
) {
    if (city.isNotBlank()) {
        onSearchStarted()
        viewModel.getData(city)
        searchViewModel.addCity(city)
    } else {
        onError("Vui lòng nhập tên thành phố.")
    }
}

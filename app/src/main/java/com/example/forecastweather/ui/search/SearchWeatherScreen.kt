package com.example.forecastweather.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.forecastweather.data.api.Constant
import com.example.forecastweather.data.api.NetworkResponse
import com.example.forecastweather.data.api.RetrofitInstance
import com.example.forecastweather.ui.daily.DailyWeatherViewModel
import com.example.forecastweather.utils.Debounce
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SearchWeatherScreen(
    viewModel: DailyWeatherViewModel,
    searchViewModel: SearchViewModel,
    onBack: () -> Unit
) {
    val weatherResult by viewModel.weatherResult.observeAsState()
    val history by searchViewModel.history.collectAsState()

    var city by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasSearched by remember { mutableStateOf(false) }
    var citySuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var showHistory by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val debounce = remember {
        Debounce<String>(
            coroutineScope = coroutineScope,
            delayMillis = 500L
        ) { input ->
            searchQuery = input
            if (input.length >= 2) {
                isSearching = true
                coroutineScope.launch {
                    try {
                        val results = RetrofitInstance.geocodingApi
                            .getCitySuggestions(input, 5, Constant.apiKey)
                        citySuggestions = results.map {
                            listOfNotNull(it.name, it.state, it.country).joinToString(", ")
                        }
                        expanded = citySuggestions.isNotEmpty()
                    } catch (e: Exception) {
                        citySuggestions = emptyList()
                        expanded = false
                    } finally {
                        isSearching = false
                    }
                }
            } else {
                citySuggestions = emptyList()
                expanded = false
                isSearching = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tìm kiếm thời tiết", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    triggerSearch(city, viewModel, searchViewModel, { hasSearched = true }, { errorMessage = it })
                    expanded = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Tìm kiếm", style = MaterialTheme.typography.titleMedium)
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize()
                .imePadding()
        ) {
            val textFieldHeight = 72.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = {
                            city = it
                            errorMessage = null
                            debounce.submit(it)
                        },
                        label = { Text("Nhập tên thành phố ") },
                        singleLine = true,
                        isError = errorMessage != null,
                        supportingText = { errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            focusManager.clearFocus()
                            triggerSearch(city, viewModel, searchViewModel, { hasSearched = true }, { errorMessage = it })
                            expanded = false
                        }),
                        trailingIcon = {
                            when {
                                isSearching -> CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                city.isNotEmpty() -> IconButton(onClick = {
                                    city = ""
                                    searchQuery = ""
                                    citySuggestions = emptyList()
                                    expanded = false
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp)) // Hoặc nhỏ hơn nếu muốn sát hơn
                if (history.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Lịch sử tìm kiếm", fontWeight = FontWeight.SemiBold)

                        Row {
                            TextButton(onClick = { showHistory = !showHistory }) {
                                Icon(Icons.Default.History, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text(if (showHistory) "Ẩn" else "Hiện")
                            }

                            TextButton(onClick = { searchViewModel.clearAll() }) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Xoá tất cả")
                            }
                        }
                    }
                }


                AnimatedVisibility(
                    visible = showHistory,
                    enter = expandVertically(tween(400)),
                    exit = shrinkVertically(tween(400))
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        mainAxisSpacing = 8.dp,
                        crossAxisSpacing = 8.dp
                    ) {
                        history.forEach { item ->
                            AssistChip(
                                onClick = {
                                    city = item
                                    searchQuery = item
                                    debounce.submit(item)
                                },
                                label = { Text(item, maxLines = 1, fontSize = 14.sp) },
                                trailingIcon = {
                                    IconButton(onClick = { searchViewModel.removeCity(item) }) {
                                        Icon(Icons.Default.Close, contentDescription = "Xoá")
                                    }
                                },
                                shape = RoundedCornerShape(24.dp)
                            )
                        }
                    }
                }

                if (weatherResult is NetworkResponse.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // Gợi ý thành phố hiển thị đè
            AnimatedVisibility(
                visible = expanded && citySuggestions.isNotEmpty(),
                enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                exit = fadeOut(tween(200)) + shrinkVertically(tween(200)),
                modifier = Modifier
                    .zIndex(1f)
                    .absoluteOffset(y = textFieldHeight + 16.dp)
                    .fillMaxWidth()
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp)
                    ) {
                        citySuggestions.forEach { suggestion ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        city = suggestion
                                        searchQuery = suggestion
                                        expanded = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Place,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(suggestion, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
    }

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

private fun triggerSearch(
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
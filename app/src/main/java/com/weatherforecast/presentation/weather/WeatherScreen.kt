import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.util.copy
import coil.compose.AsyncImage
import com.weatherforecast.domain.model.WeatherInfo
import com.weatherforecast.presentation.weather.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val state = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
            if (granted) viewModel.getWeatherByLocation()
        }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF28ADEA), Color(0xFF81D0FA), Color(0xFF81D4FA))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Weather Forecast",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3)
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(visible = true, enter = scaleIn(), exit = scaleOut()) {
                FloatingActionButton(
                    onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    containerColor = Color(0xFF093754)
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "My Location", tint = Color.White)
                }
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                ModernSearchBar(
                    query = state.query,
                    onQueryChanged = viewModel::onQueryChange,
                    onSearch = viewModel::search
                )

                Spacer(modifier = Modifier.height(24.dp))

                when {
                    state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }

                    state.weather != null -> ModernWeatherCard(state.weather!!)

                    else -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(
                            "Search a city or use location button",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            placeholder = {
                Text(
                    "Enter city name",
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.15f))  // Glass effect
                .padding(2.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,

                unfocusedIndicatorColor = Color.White.copy(alpha = 0.25f),
                focusedIndicatorColor = Color.White.copy(alpha = 0.55f),

                cursorColor = Color.White,

                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,

                focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f)
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() })
        )

        Spacer(modifier = Modifier.width(10.dp))

        FilledIconButton(
            onClick = onSearch,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color(0xFF0288D1),
                contentColor = Color.White
            ),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        }
    }
}



fun getWeatherGradient(description: String): Brush {
    val text = description.lowercase()

    return when {
        text.contains("clear") || text.contains("sun") -> {
            // Sunny / Clear sky gradient
            Brush.verticalGradient(
                listOf(
                    Color(0xFFFFD54F), // Soft yellow
                    Color(0xFFFFB300)  // Warm golden
                )
            )
        }

        text.contains("cloud") -> {
            // Cloudy gradient
            Brush.verticalGradient(
                listOf(
                    Color(0xFF90A4AE), // Soft grey-blue
                    Color(0xFF607D8B)
                )
            )
        }

        text.contains("rain") || text.contains("drizzle") -> {
            // Rainy gradient
            Brush.verticalGradient(
                listOf(
                    Color(0xFF4FC3F7), // Light rain blue
                    Color(0xFF0288D1)  // Deep rain blue
                )
            )
        }

        text.contains("storm") || text.contains("thunder") -> {
            Brush.verticalGradient(
                listOf(
                    Color(0xFF616161),
                    Color(0xFF212121)
                )
            )
        }

        text.contains("snow") -> {
            // Snow gradient
            Brush.verticalGradient(
                listOf(
                    Color(0xFFE3F2FD),
                    Color(0xFF90CAF9)
                )
            )
        }

        text.contains("mist") || text.contains("fog") || text.contains("haze") -> {
            Brush.verticalGradient(
                listOf(
                    Color(0xFFB0BEC5),
                    Color(0xFF78909C)
                )
            )
        }

        else -> {
            // Default
            Brush.verticalGradient(
                listOf(
                    Color(0xFF3F51B5),
                    Color(0xFF1A237E)
                )
            )
        }
    }
}

@Composable
fun ModernWeatherCard(weather: WeatherInfo) {

    val gradient = getWeatherGradient(weather.description)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)       // strong gradient
                .clip(RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            // Foreground clean layer (Glass without blur)
//            Box(
//                modifier = Modifier
//                    .matchParentSize()
//                    .background(
//                        Color.Black.copy(alpha = 0.25f)
//                    )
//                    .clip(RoundedCornerShape(28.dp))
//            )

            Column(
                modifier = Modifier
                    .padding(12.dp)
            ) {

                // TITLE + ICON
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            weather.city,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        )

                        Text(
                            weather.description.replaceFirstChar { it.uppercaseChar() },
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 16.sp
                        )
                    }

                    weather.icon?.let { icon ->
                        AsyncImage(
                            model = "https://openweathermap.org/img/wn/$icon@2x.png",
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // TEMP + DETAILS
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${weather.temperature}°C",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailPill("Feels Like", "${weather.feelsLike ?: "-"}°C")
                        DetailPill("Humidity", "${weather.humidity ?: "-"}%")
                        DetailPill("Wind", "${weather.windSpeed ?: "-"} m/s")
                    }
                }
            }
        }
    }
}



@Composable
fun DetailPill(label: String, value: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$label: ",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 14.sp
        )
        Text(
            value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}



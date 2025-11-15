@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {

    val state = viewModel.state

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        SearchBar(
            hint = "Search city...",
            onSearch = { viewModel.loadWeather(it) }
        )

        Spacer(Modifier.height(20.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.weather?.let { weather ->
            WeatherCard(weather)
        }

        state.error?.let {
            Text(text = it, color = Color.Red)
        }
    }
}

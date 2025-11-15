class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    var state by mutableStateOf(WeatherState())
        private set

    fun loadWeather(city: String) {
        viewModelScope.launch {
            try {
                state = state.copy(isLoading = true)
                val data = getWeatherUseCase(city)
                state = state.copy(weather = data, isLoading = false)
            } catch (e: Exception) {
                state = state.copy(error = e.message, isLoading = false)
            }
        }
    }
}

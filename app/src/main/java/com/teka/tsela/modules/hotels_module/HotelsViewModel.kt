package com.teka.tsela.modules.hotels_module

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HotelsUiState(
    val hotels: List<HotelResponse> = emptyList(),
    val featuredHotels: List<HotelResponse> = emptyList(),
    val isLoading: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val selectedFilter: String = "All", // All, Featured, Standard, Deluxe, Suite
    val searchQuery: String = "",
    val error: String? = null
)

@HiltViewModel
class HotelsViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotelsUiState())
    val uiState: StateFlow<HotelsUiState> = _uiState.asStateFlow()

    fun fetchHotels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val response = supabase.functions.invoke(
                    function = "get-hotels"
                ) {
                    method = HttpMethod.Get
                }

                if (response.status.value in 200..299) {
                    val data = response.body<HotelsResponse>()
                    Log.d("HotelsList", data.toString())
                    
                    val featuredHotels = data.hotels.filter { it.featured }
                    
                    _uiState.value = _uiState.value.copy(
                        hotels = data.hotels,
                        featuredHotels = featuredHotels,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to fetch hotels: ${response.status}"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun updateFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun updateUiState(
        update: HotelsUiState.() -> HotelsUiState
    ) {
        _uiState.update { it.update() }
    }

    fun getFilteredHotels(): List<HotelResponse> {
        val state = _uiState.value
        var filteredHotels = state.hotels

        // Apply search filter
        if (state.searchQuery.isNotBlank()) {
            filteredHotels = filteredHotels.filter { hotel ->
                hotel.name.contains(state.searchQuery, ignoreCase = true) ||
                hotel.location.contains(state.searchQuery, ignoreCase = true) ||
                hotel.city.contains(state.searchQuery, ignoreCase = true)
            }
        }

        // Apply category filter
        filteredHotels = when (state.selectedFilter) {
            "Featured" -> filteredHotels.filter { it.featured }
            "Standard" -> filteredHotels.filter { hotel ->
                hotel.room_types.any { it.category.equals("Standard", ignoreCase = true) }
            }
            "Deluxe" -> filteredHotels.filter { hotel ->
                hotel.room_types.any { it.category.equals("Deluxe", ignoreCase = true) }
            }
            "Suite" -> filteredHotels.filter { hotel ->
                hotel.room_types.any { it.category.equals("Suite", ignoreCase = true) }
            }
            else -> filteredHotels
        }

        return filteredHotels
    }
}
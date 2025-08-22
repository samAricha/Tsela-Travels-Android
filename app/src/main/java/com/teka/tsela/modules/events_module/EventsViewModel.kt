package com.teka.tsela.modules.events_module

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
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
data class EventResponse(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val start_date: String,
    val end_date: String,
    val price: Int,
    val currency: String,
    val capacity: Int,
    val available_spots: Int,
    val category: String,
    val status: String,
    val featured: Boolean,
    val registration_deadline: String,
    val image_url: String,
    val images: List<String>,
    val organizer_name: String,
    val organizer_email: String,
    val organizer_phone: String,
    val requirements: List<String>,
    val includes: List<String>,
    val excludes: List<String>?,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class EventsResponse(
    val events: List<EventResponse>,
    val count: Int
)

data class EventsUiState(
    val events: List<EventResponse> = emptyList(),
    val featuredEvents: List<EventResponse> = emptyList(),
    val isLoading: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val selectedFilter: String = "All", // All, Featured, Sports, Photography, Adventure, Cultural, etc.
    val searchQuery: String = "",
    val error: String? = null
)

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    fun fetchEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val response = supabase.functions.invoke(
                    function = "get-events"
                ) {
                    method = HttpMethod.Get
                }

                if (response.status.value in 200..299) {
                    val data = response.body<EventsResponse>()
                    Log.d("EventsList", data.toString())
                    
                    val featuredEvents = data.events.filter { it.featured }
                    
                    _uiState.value = _uiState.value.copy(
                        events = data.events,
                        featuredEvents = featuredEvents,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to fetch events: ${response.status}"
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
        update: EventsUiState.() -> EventsUiState
    ) {
        _uiState.update { it.update() }
    }

    fun getFilteredEvents(): List<EventResponse> {
        val state = _uiState.value
        var filteredEvents = state.events

        // Apply search filter
        if (state.searchQuery.isNotBlank()) {
            filteredEvents = filteredEvents.filter { event ->
                event.title.contains(state.searchQuery, ignoreCase = true) ||
                event.description.contains(state.searchQuery, ignoreCase = true) ||
                event.location.contains(state.searchQuery, ignoreCase = true) ||
                event.category.contains(state.searchQuery, ignoreCase = true) ||
                event.organizer_name.contains(state.searchQuery, ignoreCase = true)
            }
        }

        // Apply category filter
        filteredEvents = when (state.selectedFilter) {
            "Featured" -> filteredEvents.filter { it.featured }
            "Sports" -> filteredEvents.filter { it.category.equals("Sports", ignoreCase = true) }
            "Photography" -> filteredEvents.filter { it.category.equals("Photography", ignoreCase = true) }
            "Adventure" -> filteredEvents.filter { it.category.equals("Adventure", ignoreCase = true) }
            "Cultural" -> filteredEvents.filter { it.category.equals("Cultural", ignoreCase = true) }
            "Food & Drink" -> filteredEvents.filter { it.category.equals("Food & Drink", ignoreCase = true) }
            "Wildlife" -> filteredEvents.filter { it.category.equals("Wildlife", ignoreCase = true) }
            "Environmental" -> filteredEvents.filter { it.category.equals("Environmental", ignoreCase = true) }
            "Science" -> filteredEvents.filter { it.category.equals("Science", ignoreCase = true) }
            "Crafts" -> filteredEvents.filter { it.category.equals("Crafts", ignoreCase = true) }
            else -> filteredEvents
        }

        return filteredEvents
    }

    fun getAvailableCategories(): List<String> {
        val categories = _uiState.value.events.map { it.category }.distinct().sorted()
        return listOf("All", "Featured") + categories
    }
}
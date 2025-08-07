package com.teka.tsela.modules.destinations_module

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

data class DestinationUiState(
    val destinations: List<DestinationResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val total: Int = 0
)

@HiltViewModel
class DestinationViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(DestinationUiState())
    val uiState: StateFlow<DestinationUiState> = _uiState.asStateFlow()

    fun fetchDestinations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val response = supabase.functions.invoke(
                    function = "get-destinations"
                ) {
                    method = HttpMethod.Get
                }

                if (response.status.value in 200..299) {
                    val data = response.body<DestinationsApiResponse>()
                    Log.d("DestinationsList", data.toString())
                    _uiState.value = _uiState.value.copy(
                        destinations = data.destinations,
                        total = data.total,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to fetch destinations: ${response.status}"
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun updateUiState(
        update: DestinationUiState.() -> DestinationUiState
    ) {
        _uiState.update { it.update() }
    }
}
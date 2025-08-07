package com.teka.tsela.modules.excursions_module

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

data class ExcursionsUiState(
    val excursions: List<ExcursionResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: String = "All"
)

@HiltViewModel
class ExcursionsViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExcursionsUiState())
    val uiState: StateFlow<ExcursionsUiState> = _uiState.asStateFlow()

    fun fetchExcursions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val response = supabase.functions.invoke(
                    function = "get-excursions"
                ) {
                    method = HttpMethod.Get
                }

                if (response.status.value in 200..299) {
                    val data = response.body<ExcursionsApiResponse>()
                    Log.d("ExcursionsList", data.toString())
                    _uiState.value = _uiState.value.copy(
                        excursions = data.excursions,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to fetch excursions: ${response.status}"
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

    fun filterByCategory(category: String) {
        _uiState.update { 
            it.copy(selectedCategory = category)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun updateUiState(
        update: ExcursionsUiState.() -> ExcursionsUiState
    ) {
        _uiState.update { it.update() }
    }
}
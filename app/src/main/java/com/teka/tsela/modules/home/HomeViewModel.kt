package com.teka.tsela.modules.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "",
    val isLoading: Boolean = false,
    val destinationOfDay: DestinationOfDay? = null,
    val recentMemory: RecentMemory? = null,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    // Inject your repositories here
    // private val userRepository: UserRepository,
    // private val destinationsRepository: DestinationsRepository,
    // private val memoriesRepository: MemoriesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Load user profile
                loadUserProfile()
                
                // Load destination of the day or recent memory
                // Prioritize recent memory if available, otherwise show destination of the day
                val recentMemory = loadRecentMemory()
                
                if (recentMemory != null) {
                    _uiState.update { it.copy(recentMemory = recentMemory) }
                } else {
                    val destinationOfDay = loadDestinationOfDay()
                    _uiState.update { it.copy(destinationOfDay = destinationOfDay) }
                }
                
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load home data"
                    )
                }
            }
        }
    }

    private suspend fun loadUserProfile() {
        try {
            // TODO: Implement actual user profile loading
            // val userProfile = userRepository.getCurrentUser()
            // _uiState.update { it.copy(userName = userProfile.firstName) }
            
            // Mock data for now
            _uiState.update { it.copy(userName = "Alex") }
        } catch (e: Exception) {
            // Handle error silently for user name, not critical
        }
    }

    private suspend fun loadRecentMemory(): RecentMemory? {
        return try {
            // TODO: Implement actual recent memory loading
            // val memories = memoriesRepository.getRecentMemories(limit = 1)
            // memories.firstOrNull()?.toRecentMemory()
            
            // Mock data for now - return null to show destination of day instead
            // You can uncomment this to test the recent memory UI:
            /*
            RecentMemory(
                id = "1",
                title = "Sunset at Santorini",
                location = "Santorini, Greece",
                imageUrl = "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff",
                date = "Dec 15, 2024",
                aiCaption = "This stunning sunset captures the iconic blue-domed churches of Santorini against the Aegean Sea, created by volcanic activity over thousands of years."
            )
            */
            null
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun loadDestinationOfDay(): DestinationOfDay? {
        return try {
            // TODO: Implement actual destination of day loading
            // val destination = destinationsRepository.getDestinationOfDay()
            // destination?.toDestinationOfDay()
            
            // Mock data for now
            DestinationOfDay(
                name = "Kyoto",
                country = "Japan",
                imageUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e",
                fact = "Home to over 2,000 temples and shrines, Kyoto was Japan's imperial capital for over 1,000 years.",
                temperature = "18°C"
            )
        } catch (e: Exception) {
            null
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun refreshHomeData() {
        loadHomeData()
    }
}
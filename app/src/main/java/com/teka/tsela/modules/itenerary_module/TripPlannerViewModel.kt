package com.teka.tsela.modules.itenerary_module

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
data class TripBookingRequest(
    val name: String,
    val email: String,
    val phone: String,
    val destination_interest: List<String>,
    val experience_type: List<String>,
    val start_date: String,
    val duration_days: Int,
    val group_size: Int,
    val budget: Int,
    val anything_else: String
)

@Serializable
data class TripBookingResponse(
    val success: Boolean,
    val itinerary: String? = null,
    val booking_id: String? = null,
    val error: String? = null
)

data class PersonalInfo(
    val name: String = "",
    val email: String = "",
    val phone: String = "+254"
)

data class Preferences(
    val destinations: Set<String> = emptySet(),
    val experiences: Set<String> = emptySet()
)

data class TripDetails(
    val startDate: String = "",
    val duration: String = "",
    val groupSize: Int = 1,
    val budget: Int = 0,
    val specialRequests: String = ""
)

data class FormErrors(
    val name: String? = null,
    val email: String? = null,
    val destinations: String? = null,
    val experiences: String? = null,
    val startDate: String? = null,
    val duration: String? = null,
    val groupSize: String? = null,
    val budget: String? = null,
    val general: String? = null
)

data class TripPlannerUiState(
    val currentStep: Int = 0,
    val personalInfo: PersonalInfo = PersonalInfo(),
    val preferences: Preferences = Preferences(),
    val tripDetails: TripDetails = TripDetails(),
    val errors: FormErrors = FormErrors(),
    val isLoading: Boolean = false,
    val isSubmitted: Boolean = false,
    val generatedItinerary: String? = null,
    val bookingId: String? = null,
    val error: String? = null
)

@HiltViewModel
class TripPlannerViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripPlannerUiState())
    val uiState: StateFlow<TripPlannerUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "TripPlannerViewModel"
        private const val MAX_STEPS = 3
        
        val DESTINATIONS = listOf(
            "🦁 Maasai Mara",
            "🏔️ Mount Kenya", 
            "🌊 Diani Beach",
            "🐘 Amboseli",
            "🦩 Lake Nakuru",
            "🏝️ Lamu Island",
            "🌋 Hell's Gate",
            "🦒 Nairobi"
        )
        
        val EXPERIENCES = listOf(
            "🚁 Safari Adventure",
            "🏖️ Beach Relaxation",
            "🥾 Mountain Hiking",
            "🎭 Cultural Tours",
            "📸 Photography",
            "🍽️ Food & Wine"
        )
        
        val DURATIONS = listOf(
            "3-4 Days" to 4,
            "1 Week" to 7,
            "2 Weeks" to 14,
            "3+ Weeks" to 21
        )
    }

    fun nextStep() {
        if (validateCurrentStep()) {
            _uiState.update { state ->
                if (state.currentStep < MAX_STEPS - 1) {
                    state.copy(
                        currentStep = state.currentStep + 1,
                        errors = FormErrors()
                    )
                } else {
                    state
                }
            }
        }
    }

    fun previousStep() {
        _uiState.update { state ->
            if (state.currentStep > 0) {
                state.copy(
                    currentStep = state.currentStep - 1,
                    errors = FormErrors()
                )
            } else {
                state
            }
        }
    }

    fun updatePersonalInfo(update: PersonalInfo.() -> PersonalInfo) {
        _uiState.update { state ->
            state.copy(
                personalInfo = state.personalInfo.update(),
                errors = state.errors.copy(
                    name = null,
                    email = null,
                    general = null
                )
            )
        }
    }

    fun updatePreferences(update: Preferences.() -> Preferences) {
        _uiState.update { state ->
            state.copy(
                preferences = state.preferences.update(),
                errors = state.errors.copy(
                    destinations = null,
                    experiences = null,
                    general = null
                )
            )
        }
    }

    fun updateTripDetails(update: TripDetails.() -> TripDetails) {
        _uiState.update { state ->
            state.copy(
                tripDetails = state.tripDetails.update(),
                errors = state.errors.copy(
                    startDate = null,
                    duration = null,
                    groupSize = null,
                    budget = null,
                    general = null
                )
            )
        }
    }

    fun toggleDestination(destination: String) {
        updatePreferences { 
            copy(
                destinations = if (destinations.contains(destination)) {
                    destinations - destination
                } else {
                    destinations + destination
                }
            )
        }
    }

    fun toggleExperience(experience: String) {
        updatePreferences { 
            copy(
                experiences = if (experiences.contains(experience)) {
                    experiences - experience
                } else {
                    experiences + experience
                }
            )
        }
    }

    fun selectDuration(duration: String) {
        updateTripDetails { copy(duration = duration) }
    }

    private fun validateCurrentStep(): Boolean {
        val state = _uiState.value
        val errors = mutableMapOf<String, String>()

        when (state.currentStep) {
            0 -> { // Personal Info
                if (state.personalInfo.name.isBlank()) {
                    errors["name"] = "Name is required"
                }
                if (state.personalInfo.email.isBlank()) {
                    errors["email"] = "Email is required"
                } else if (!isValidEmail(state.personalInfo.email)) {
                    errors["email"] = "Please enter a valid email address"
                }
            }
            1 -> { // Preferences
                if (state.preferences.destinations.isEmpty()) {
                    errors["destinations"] = "Please select at least one destination"
                }
                if (state.preferences.experiences.isEmpty()) {
                    errors["experiences"] = "Please select at least one experience"
                }
            }
            2 -> { // Trip Details
                if (state.tripDetails.startDate.isBlank()) {
                    errors["startDate"] = "Please select a start date"
                }
                if (state.tripDetails.duration.isBlank()) {
                    errors["duration"] = "Please select trip duration"
                }
                if (state.tripDetails.groupSize < 1) {
                    errors["groupSize"] = "Group size must be at least 1"
                } else if (state.tripDetails.groupSize > 20) {
                    errors["groupSize"] = "Maximum group size is 20"
                }
                if (state.tripDetails.budget < 10000) {
                    errors["budget"] = "Minimum budget is KSh 10,000"
                } else if (state.tripDetails.budget > 10000000) {
                    errors["budget"] = "Maximum budget is KSh 10,000,000"
                }
            }
        }

        if (errors.isNotEmpty()) {
            _uiState.update { 
                it.copy(
                    errors = FormErrors(
                        name = errors["name"],
                        email = errors["email"],
                        destinations = errors["destinations"],
                        experiences = errors["experiences"],
                        startDate = errors["startDate"],
                        duration = errors["duration"],
                        groupSize = errors["groupSize"],
                        budget = errors["budget"]
                    )
                )
            }
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun submitTripPlan() {
        if (!validateCurrentStep()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val state = _uiState.value
                val durationDays = DURATIONS.find { it.first == state.tripDetails.duration }?.second ?: 7

                val request = TripBookingRequest(
                    name = state.personalInfo.name,
                    email = state.personalInfo.email,
                    phone = state.personalInfo.phone,
                    destination_interest = state.preferences.destinations.toList(),
                    experience_type = state.preferences.experiences.toList(),
                    start_date = state.tripDetails.startDate,
                    duration_days = durationDays,
                    group_size = state.tripDetails.groupSize,
                    budget = state.tripDetails.budget,
                    anything_else = state.tripDetails.specialRequests
                )

                Log.d(TAG, "Submitting trip plan: $request")

                val response = supabase.functions.invoke(
                    function = "process-trip-booking"
                ) {
                    method = HttpMethod.Post
                    header("Content-Type", "application/json")
                    header("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9pZ2hoY2NtcWNja29zeW5qaXluIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDQzOTY2MjksImV4cCI6MjA1OTk3MjYyOX0.mVdgwOEOfiERWpbg-GHD8dW2CODH5VdxPs0ne2u7F24")
                    setBody(request)
                }


                if (response.status.value in 200..299) {
                    val data = response.body<TripBookingResponse>()
                    Log.d(TAG, "Trip booking response: $data")
                    
                    if (data.success && data.itinerary != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSubmitted = true,
                                generatedItinerary = data.itinerary,
                                bookingId = data.booking_id
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = data.error ?: "Failed to generate itinerary"
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Server error: ${response.status.description}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error submitting trip plan", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to submit trip plan"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetForm() {
        _uiState.value = TripPlannerUiState()
    }
}
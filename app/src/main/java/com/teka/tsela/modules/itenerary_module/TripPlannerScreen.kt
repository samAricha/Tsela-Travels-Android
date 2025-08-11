package com.teka.tsela.modules.itenerary_module

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.teka.tsela.ui.theme.TextSizeXLarge
import com.teka.tsela.ui.theme.TextSizeXXLarge
import com.teka.tsela.utils.ui_components.CustomTopAppBar
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPlannerScreen(
    navController: NavController,
    viewModel: TripPlannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState.isLoading
    val error = uiState.error

    // Error handling
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            Timber.e("Trip Planner error: $errorMessage")
        }
    }

    // Success handling - show itinerary
    if (uiState.isSubmitted && uiState.generatedItinerary != null) {
        ItineraryScreen(
            itinerary = uiState.generatedItinerary!!,
            bookingId = uiState.bookingId,
            onBackClick = { 
                viewModel.resetForm()
            },
            onNewTrip = {
                viewModel.resetForm()
            }
        )
        return
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = {
                    Text(
                        "Trip Planner",
                        fontSize = TextSizeXXLarge,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Step Indicator
                StepIndicator(
                    currentStep = uiState.currentStep,
                    totalSteps = 3,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )

                // Form Content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (uiState.currentStep) {
                        0 -> PersonalInfoStep(
                            personalInfo = uiState.personalInfo,
                            errors = uiState.errors,
                            onUpdate = viewModel::updatePersonalInfo
                        )
                        1 -> PreferencesStep(
                            preferences = uiState.preferences,
                            errors = uiState.errors,
                            onToggleDestination = viewModel::toggleDestination,
                            onToggleExperience = viewModel::toggleExperience
                        )
                        2 -> TripDetailsStep(
                            tripDetails = uiState.tripDetails,
                            errors = uiState.errors,
                            onUpdate = viewModel::updateTripDetails,
                            onSelectDuration = viewModel::selectDuration
                        )
                    }
                }

                // Navigation Buttons
                NavigationButtons(
                    currentStep = uiState.currentStep,
                    isLoading = isLoading,
                    onPrevious = viewModel::previousStep,
                    onNext = viewModel::nextStep,
                    onSubmit = viewModel::submitTripPlan,
                    modifier = Modifier.padding(24.dp)
                )
            }

            // Loading Overlay
            if (isLoading) {
                LoadingOverlay()
            }

            // Error Display
            error?.let { errorMessage ->
                ErrorSnackbar(
                    message = errorMessage,
                    onDismiss = viewModel::clearError,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    val orangeGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFf97316), // Orange
            Color(0xFFef4444)  // Red
        )
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { step ->
            val isActive = step <= currentStep
            val isCompleted = step < currentStep

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) {
                            orangeGradient
                        } else {
                            SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        },
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(8.dp),
                        tint = Color.White
                    )
                }
            }

            if (step < totalSteps - 1) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(
                            if (step < currentStep) {
                                orangeGradient
                            } else {
                                SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            },
                            shape = RectangleShape
                        )
                )
            }
        }
    }
}

@Composable
private fun PersonalInfoStep(
    personalInfo: PersonalInfo,
    errors: FormErrors,
    onUpdate: (PersonalInfo.() -> PersonalInfo) -> Unit
) {
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            StepHeader(
                title = "Tell us about yourself",
                subtitle = "We'll use this information to personalize your itinerary"
            )
        }

        item {
            FormCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Name Field
                    OutlinedTextField(
                        value = personalInfo.name,
                        onValueChange = { onUpdate { copy(name = it) } },
                        label = { Text("Full Name") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errors.name != null,
                        supportingText = errors.name?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Email Field
                    OutlinedTextField(
                        value = personalInfo.email,
                        onValueChange = { onUpdate { copy(email = it) } },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errors.email != null,
                        supportingText = errors.email?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Phone Field
                    OutlinedTextField(
                        value = personalInfo.phone,
                        onValueChange = { 
                            if (it.startsWith("+254")) {
                                onUpdate { copy(phone = it) }
                            }
                        },
                        label = { Text("Phone Number (Optional)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PreferencesStep(
    preferences: Preferences,
    errors: FormErrors,
    onToggleDestination: (String) -> Unit,
    onToggleExperience: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            StepHeader(
                title = "What interests you?",
                subtitle = "Select your preferred destinations and experiences"
            )
        }

        item {
            FormCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Destinations Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Destinations",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "(${preferences.destinations.size} selected)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (errors.destinations != null) {
                            Text(
                                text = errors.destinations!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Fixed height LazyVerticalGrid with 2 columns for destinations
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.height(200.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(TripPlannerViewModel.DESTINATIONS) { destination ->
                                SelectableBadge(
                                    text = destination,
                                    isSelected = preferences.destinations.contains(destination),
                                    onClick = { onToggleDestination(destination) }
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )

                    // Experiences Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Experiences",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "(${preferences.experiences.size} selected)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (errors.experiences != null) {
                            Text(
                                text = errors.experiences!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Fixed height LazyVerticalGrid with 2 columns for experiences
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.height(200.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(TripPlannerViewModel.EXPERIENCES) { experience ->
                                SelectableBadge(
                                    text = experience,
                                    isSelected = preferences.experiences.contains(experience),
                                    onClick = { onToggleExperience(experience) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripDetailsStep(
    tripDetails: TripDetails,
    errors: FormErrors,
    onUpdate: (TripDetails.() -> TripDetails) -> Unit,
    onSelectDuration: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }


    val interactionSource = remember { MutableInteractionSource() }

    // Listen for clicks on the date field
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                showDatePicker = true
            }
        }
    }

    // Handle date selection
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = formatter.format(Date(millis))
            onUpdate { copy(startDate = date) }
        }
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            StepHeader(
                title = "Plan your trip",
                subtitle = "Tell us when and how you'd like to travel"
            )
        }

        item {
            FormCard {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Start Date
                    OutlinedTextField(
                        value = tripDetails.startDate.takeIf { it.isNotBlank() }?.let {
                            try {
                                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                outputFormat.format(inputFormat.parse(it) ?: Date())
                            } catch (e: Exception) {
                                it
                            }
                        } ?: "",
                        onValueChange = { },
                        label = { Text("Start Date") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null
                            )
                        },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = errors.startDate != null,
                        supportingText = errors.startDate?.let { { Text(it) } },
                        shape = RoundedCornerShape(12.dp),
                        interactionSource = interactionSource // Use the interaction source
                    )


                    // Duration Selection
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Trip Duration",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )

                        if (errors.duration != null) {
                            Text(
                                text = errors.duration!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Fixed height LazyVerticalGrid with 2 columns for durations
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.height(120.dp), // Smaller height since durations are fewer
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(TripPlannerViewModel.DURATIONS) { (duration, _) ->
                                SelectableBadge(
                                    text = duration,
                                    isSelected = tripDetails.duration == duration,
                                    onClick = { onSelectDuration(duration) }
                                )
                            }
                        }
                    }

                    // Group Size
                    OutlinedTextField(
                        value = if (tripDetails.groupSize > 0) tripDetails.groupSize.toString() else "",
                        onValueChange = {
                            val size = it.toIntOrNull() ?: 0
                            onUpdate { copy(groupSize = size) }
                        },
                        label = { Text("Group Size") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errors.groupSize != null,
                        supportingText = errors.groupSize?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Budget
                    OutlinedTextField(
                        value = if (tripDetails.budget > 0) tripDetails.budget.toString() else "",
                        onValueChange = {
                            val budget = it.toIntOrNull() ?: 0
                            onUpdate { copy(budget = budget) }
                        },
                        label = { Text("Budget (KSh)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errors.budget != null,
                        supportingText = errors.budget?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Special Requests
                    OutlinedTextField(
                        value = tripDetails.specialRequests,
                        onValueChange = { onUpdate { copy(specialRequests = it) } },
                        label = { Text("Special Requests (Optional)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Notes,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    }


    if (showDatePicker) {
        MyDatePickerDialog(
            datePickerState = datePickerState,
            onDateSelected = { /* handle if needed */ },
            onDismiss = { showDatePicker = false }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyDatePickerDialog(
    datePickerState: DatePickerState, // Accept state as parameter
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}



@Composable
private fun StepHeader(
    title: String,
    subtitle: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = TextSizeXLarge,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FormCard(
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectableBadge(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val orangeGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFf97316), // Orange
            Color(0xFFef4444)  // Red
        )
    )

    Surface(
        onClick = onClick,
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) null else BorderStroke(
            1.dp, 
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = if (isSelected) {
                Modifier.background(orangeGradient)
            } else {
                Modifier
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NavigationButtons(
    currentStep: Int,
    isLoading: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orangeGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFf97316), // Orange
            Color(0xFFef4444)  // Red
        )
    )

    val disabledGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFf97316).copy(alpha = 0.7f), // Orange with reduced opacity
            Color(0xFFef4444).copy(alpha = 0.7f)  // Red with reduced opacity
        )
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Previous Button
        if (currentStep > 0) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Previous")
            }
        }

        // Next/Submit Button
        Button(
            onClick = if (currentStep == 2) onSubmit else onNext,
            modifier = Modifier
                .weight(if (currentStep > 0) 1f else 2f)
                .height(56.dp), // Add consistent height
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp) // Remove default padding
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = if (!isLoading) orangeGradient else disabledGradient,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading && currentStep == 2) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Text(
                            text = "🤖 Creating Your Itinerary...",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (currentStep == 2) "Create Itinerary" else "Next",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (currentStep == 2) Icons.Default.Send else Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }


    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp,
                    color = Color(0xFFf97316)
                )
                Text(
                    "🤖 Creating Your Itinerary...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Text(
                    "This may take a few moments",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ErrorSnackbar(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier.padding(16.dp),
        action = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        },
        dismissAction = {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss"
                )
            }
        }
    ) {
        Text(message)
    }
}




@Composable
private fun ItineraryScreen(
    itinerary: String,
    bookingId: String?,
    onBackClick: () -> Unit,
    onNewTrip: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "✨ Your Itinerary Ready!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFf97316)
                    )
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                bookingId?.let { id ->
                    Text(
                        "Booking ID: $id",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Itinerary Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = itinerary,
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.bodyMedium,
//                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
                )
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onNewTrip,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Trip")
            }

            Button(
                onClick = { /* Handle share/save */ },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFf97316)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share", color = Color.White)
            }
        }
    }
}
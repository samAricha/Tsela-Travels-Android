package com.teka.tsela.modules.landing

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.teka.tsela.R
import com.teka.tsela.core.navigation.AUTH_GRAPH_ROUTE
import com.teka.tsela.core.navigation.AppScreens
import com.teka.tsela.ui.theme.TextSizeXLarge
import com.teka.tsela.ui.theme.TextSizeXXLarge

@Composable
fun LandingScreen(
    navController: NavController
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.05f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Brand Section
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(800, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(800))
            ) {
                BrandSection()
            }

            Spacer(modifier = Modifier.weight(1f))

            // Welcome Text Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(1000, delayMillis = 400)
                ) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(800, delayMillis = 400, easing = EaseOutCubic)
                )
            ) {
                WelcomeSection()
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Action Buttons
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(800, delayMillis = 600, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 600))
            ) {
                ActionButtonsSection(
                    onCreateItineraryClick = {
                        // Navigate to create itinerary screen
                         navController.navigate(AppScreens.TripPlannerScreen.route)
                    },
                    onLoginClick = {
                        // Navigate to login screen
                         navController.navigate(AUTH_GRAPH_ROUTE)
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Features Preview
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(1000, delayMillis = 800)
                )
            ) {
                FeaturesPreview()
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BrandSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Logo with background
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(30.dp),
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tsela_image),
                    contentDescription = "Tsela Logo",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Brand Name
        Text(
            text = "TSELA",
            fontSize = 36.sp,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 2.sp
        )

        // Tagline
        Text(
            text = "Your Journey, Perfectly Planned",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun WelcomeSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Welcome to Tsela",
            fontSize = TextSizeXXLarge,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Discover amazing destinations, create personalized itineraries, and book the perfect accommodations for your next adventure.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun ActionButtonsSection(
    onCreateItineraryClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Primary Action - Create Itinerary
        Button(
            onClick = onCreateItineraryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 12.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Create Itinerary",
                    fontSize = TextSizeXLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Secondary Action - Login
        OutlinedButton(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 2.dp
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Login,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Login to Account",
                    fontSize = TextSizeXLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun FeaturesPreview() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FeatureItem(
            icon = Icons.Default.Hotel,
            title = "Hotels",
            description = "Find & Book"
        )
        
        FeatureItem(
            icon = Icons.Default.Flight,
            title = "Flights",
            description = "Best Deals"
        )
        
        FeatureItem(
            icon = Icons.Default.Map,
            title = "Itinerary",
            description = "Plan Trip"
        )
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.width(80.dp)
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
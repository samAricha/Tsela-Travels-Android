package com.teka.tsela.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chalet
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Tour
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?
) {
    val items = listOf(
        BottomNavItem(
            title = "Home",
            icon = Icons.Default.Chalet,
            route = AppScreens.HomeScreen.route
        ),
        BottomNavItem(
            title = "Destinations",
            icon = Icons.Default.Place,
            route = AppScreens.DestinationsScreen.route
        ),
        BottomNavItem(
            title = "Excursions",
            icon = Icons.Default.Tour,
            route = AppScreens.ExcursionsScreen.route
        ),
        BottomNavItem(
            title = "Hotels",
            icon = Icons.Default.Hotel,
            route = AppScreens.HotelsListScreen.route
        )
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
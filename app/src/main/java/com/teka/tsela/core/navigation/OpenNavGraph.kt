package com.teka.tsela.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.teka.tsela.modules.auth_module.login.LoginScreen
import com.teka.tsela.modules.itenerary_module.TripPlannerScreen

fun NavGraphBuilder.openNavGraph(
    navController: NavHostController
){

    navigation(
        startDestination = AppScreens.TripPlannerScreen.route,
        route = OPEN_GRAPH_ROUTE
    ) {
        composable(
            route = AppScreens.TripPlannerScreen.route,
            enterTransition = ScreenTransitions.enterTransition,
            exitTransition = ScreenTransitions.exitTransition,
            popEnterTransition = ScreenTransitions.popEnterTransition,
            popExitTransition = ScreenTransitions.popExitTransition,
            content = {
                TripPlannerScreen(
                    navController = navController
                )
            },

        )

    }
}
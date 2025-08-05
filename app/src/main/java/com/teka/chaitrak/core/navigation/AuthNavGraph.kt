package com.teka.chaitrak.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.teka.chaitrak.modules.auth_module.login.LoginScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController
){

    navigation(
        startDestination = AppScreens.LoginScreen.route,
        route = AUTH_GRAPH_ROUTE
    ) {
        composable(
            route = AppScreens.LoginScreen.route,
            enterTransition = ScreenTransitions.enterTransition,
            exitTransition = ScreenTransitions.exitTransition,
            popEnterTransition = ScreenTransitions.popEnterTransition,
            popExitTransition = ScreenTransitions.popExitTransition,
            content = {
                LoginScreen(
                    navigator = navController
                )
            },

        )

    }
}
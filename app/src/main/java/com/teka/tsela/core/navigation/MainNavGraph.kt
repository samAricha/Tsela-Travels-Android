package com.teka.tsela.core.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.teka.tsela.modules.chat_module.ChatScreen
import com.teka.tsela.modules.collections.collections_list.CollectionsScreen
import com.teka.tsela.modules.collections.collections_form.CollectionForm
import com.teka.tsela.modules.destinations_module.DestinationsScreen
import com.teka.tsela.modules.excursions_module.ExcursionsScreen
import com.teka.tsela.modules.hotels_module.HotelsScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(
    navController: NavHostController = rememberNavController(),
) {

    NavHost(
        navController = navController,
        startDestination = AppScreens.DestinationsScreen.route,
        route = MAIN_GRAPH_ROUTE
    ) {

        composable(
            route = AppScreens.CollectionsListScreen.route,
            enterTransition = ScreenTransitions.enterTransition,
            exitTransition = ScreenTransitions.exitTransition,
            popEnterTransition = ScreenTransitions.popEnterTransition,
            popExitTransition = ScreenTransitions.popExitTransition,
        ){
            CollectionsScreen(navController)
        }

        composable(
            route = AppScreens.CollectionsFormScreen.route,
            enterTransition = ScreenTransitions.enterTransition,
            exitTransition = ScreenTransitions.exitTransition,
            popEnterTransition = ScreenTransitions.popEnterTransition,
            popExitTransition = ScreenTransitions.popExitTransition,
        ){
            CollectionForm(navController)
        }

        composable(
            route = AppScreens.DestinationsScreen.route,
            enterTransition = ScreenTransitions.enterTransition,
            exitTransition = ScreenTransitions.exitTransition,
            popEnterTransition = ScreenTransitions.popEnterTransition,
            popExitTransition = ScreenTransitions.popExitTransition,
        ){
            DestinationsScreen(navController)
        }

        composable(
            route = AppScreens.DestinationsDetailsScreen.route,
            enterTransition = ScreenTransitions.enterTransition,
            exitTransition = ScreenTransitions.exitTransition,
            popEnterTransition = ScreenTransitions.popEnterTransition,
            popExitTransition = ScreenTransitions.popExitTransition,
        ){
//            DestinationsDetailsScreen(navController)
        }

        composable(
            route = AppScreens.ExcursionsScreen.route,
            enterTransition = ScreenTransitions.enterTransition,
            exitTransition = ScreenTransitions.exitTransition,
            popEnterTransition = ScreenTransitions.popEnterTransition,
            popExitTransition = ScreenTransitions.popExitTransition,
        ){
            ExcursionsScreen(navController)
        }

        composable(
            route = AppScreens.ExcursionsDetailsScreen.route,
            enterTransition = ScreenTransitions.enterTransition,
            exitTransition = ScreenTransitions.exitTransition,
            popEnterTransition = ScreenTransitions.popEnterTransition,
            popExitTransition = ScreenTransitions.popExitTransition,
        ){
//            ExcursionsDetailsScreen(navController)
        }

        composable(
            route = AppScreens.HotelsListScreen.route,
            enterTransition = ScreenTransitions.enterTransition,
            exitTransition = ScreenTransitions.exitTransition,
            popEnterTransition = ScreenTransitions.popEnterTransition,
            popExitTransition = ScreenTransitions.popExitTransition,
        ){
            HotelsScreen(navController)
        }

        composable(
            route = AppScreens.HotelDetailsScreen.route,
            enterTransition = ScreenTransitions.enterTransition,
            exitTransition = ScreenTransitions.exitTransition,
            popEnterTransition = ScreenTransitions.popEnterTransition,
            popExitTransition = ScreenTransitions.popExitTransition,
        ){
//            ExcursionsDetailsScreen(navController)
        }

        composable(
            route = AppScreens.ChatScreen.route,
            enterTransition = ScreenTransitions.enterTransition,
            exitTransition = ScreenTransitions.exitTransition,
            popEnterTransition = ScreenTransitions.popEnterTransition,
            popExitTransition = ScreenTransitions.popExitTransition,
        ){
            ChatScreen(navController)
        }


    }
}
package com.teka.tsela.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.teka.tsela.core.MainAppScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootNavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        route = ROOT_GRAPH_ROUTE
    ) {
        authNavGraph(navController = navController)
        openNavGraph(navController = navController)

        composable(route = To_MAIN_GRAPH_ROUTE) {
            MainAppScreen()
        }

    }
}
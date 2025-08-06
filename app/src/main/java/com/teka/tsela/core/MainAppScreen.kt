package com.teka.tsela.core

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.teka.tsela.core.navigation.BottomNavigationBar
import com.teka.tsela.core.navigation.MainNavGraph
import com.teka.tsela.core.navigation.rememberAppState


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val newBackStackEntry by navController.currentBackStackEntryAsState()
    val appState = rememberAppState(navHostController = navController)
    val route = newBackStackEntry?.destination?.route
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = appState.navHostController,
                currentRoute = route
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainNavGraph(navController = appState.navHostController)
        }
    }
}


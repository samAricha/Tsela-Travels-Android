package com.teka.chaitrak.core.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavBackStackEntry
import com.teka.chaitrak.ui.animations.scaleIntoContainer
import com.teka.chaitrak.ui.animations.scaleOutOfContainer

@OptIn(ExperimentalAnimationApi::class)
object ScreenTransitions {
    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
        scaleIntoContainer()
    }
    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
        scaleOutOfContainer(direction = AnimatedContentTransitionScope.SlideDirection.Right)
    }
    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
        scaleIntoContainer(direction = AnimatedContentTransitionScope.SlideDirection.Left)
    }
    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
        scaleOutOfContainer()
    }
}

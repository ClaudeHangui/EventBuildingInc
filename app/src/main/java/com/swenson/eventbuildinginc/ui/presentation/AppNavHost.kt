package com.swenson.eventbuildinginc.ui.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.swenson.eventbuildinginc.ui.view.CategoriesScreen
import com.swenson.eventbuildinginc.ui.view.CategoryDetailScreen
import com.swenson.eventbuildinginc.ui.view.SavedEventScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(modifier: Modifier, navController: NavHostController) {
    AnimatedNavHost(
        navController = navController,
        modifier = modifier,
        startDestination = Destinations.Categories.route
    ) {
        composable(
            Destinations.Categories.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Destinations.CategoryDetails.route + "/{categoryId}/{categoryName}" -> {
                        enterTransition
                    }
                    Destinations.SavedEvent.route -> {
                        enterTransition
                    }
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Destinations.CategoryDetails.route + "/{categoryId}/{categoryName}" -> {
                        exitTransition
                    }
                    Destinations.SavedEvent.route -> {
                        exitTransition
                    }
                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    Destinations.CategoryDetails.route + "/{categoryId}/{categoryName}" -> {
                        popEnterTransition
                    }
                    Destinations.SavedEvent.route -> {
                        popEnterTransition
                    }
                    else -> null
                }
            },
        ) {
            CategoriesScreen(navController = navController)
        }

        composable(
            route = Destinations.CategoryDetails.withArgsFormat(
                Destinations.CategoryDetails.categoryId,
                Destinations.CategoryDetails.categoryName
            ),
            arguments = listOf(
                navArgument(Destinations.CategoryDetails.categoryId) {
                    type = NavType.IntType
                },
                navArgument(Destinations.CategoryDetails.categoryName) {
                    type = NavType.StringType
                }
            ),
            enterTransition = {
                when (initialState.destination.route) {
                    Destinations.Categories.route -> enterTransition
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Destinations.Categories.route -> exitTransition
                    else -> null
                }
            }
        ) { navBackStackEntry ->
            val args = navBackStackEntry.arguments
            CategoryDetailScreen(
                categoryId = args?.getInt(Destinations.CategoryDetails.categoryId) ?: -1,
                categoryName = args?.getString(Destinations.CategoryDetails.categoryName) ?: "",
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Destinations.SavedEvent.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Destinations.Categories.route -> enterTransition
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Destinations.Categories.route -> exitTransition
                    else -> null
                }
            },
            popExitTransition = {
                when (initialState.destination.route) {
                    Destinations.Categories.route -> popExitTransition
                    else -> null
                }
            }

        ) { navBackStackEntry ->
            SavedEventScreen(
                navigateBack = {
                    navController.popBackStack(Destinations.Categories.route, inclusive = false)
                },
            )
        }
    }

}
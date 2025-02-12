package com.thebipolaroptimist.stuffrandomizer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryCreationScreen
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryEditScreen
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryListScreen
import com.thebipolaroptimist.stuffrandomizer.ui.HomeScreen
import com.thebipolaroptimist.stuffrandomizer.ui.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.ui.PartyCreationScreen
import com.thebipolaroptimist.stuffrandomizer.ui.PartyEditScreen
import com.thebipolaroptimist.stuffrandomizer.ui.PartyListScreen
import kotlinx.serialization.Serializable

@Composable
fun StuffRandomizerNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = HomeNav) {
        composable<HomeNav> {
            //TODO #16: Make sure this works without the backstack entry
            HomeScreen(
                toPartyCreation = { navController.navigate(PartyCreationNav) },
                toCategoryList = { navController.navigate(CategoryListNav) },
                toPartyList = { navController.navigate(PartyListNav) })
        }
        composable<CategoryCreationNav> { backStackEntry ->
            val homeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HomeNav)
            }
            val viewModel = hiltViewModel<MainViewModel>(homeEntry)
            CategoryCreationScreen(viewModel,
                navigateBack = { navController.popBackStack() },
                toCategoryList = {
                    navController.navigate(CategoryListNav) {
                        popUpTo(HomeNav)
                    }
                }
            )
        }
        composable<CategoryEditNav> { backStackEntry ->
            val categoryEdit = backStackEntry.toRoute<CategoryEditNav>()
            val homeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HomeNav)
            }
            val viewModel = hiltViewModel<MainViewModel>(homeEntry)
            CategoryEditScreen(
                viewModel,
                id = categoryEdit.id,
                navigateBack = { navController.popBackStack() })

        }
        composable<CategoryListNav> { backStackEntry ->
            val homeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HomeNav)
            }
            val viewModel = hiltViewModel<MainViewModel>(homeEntry)
            CategoryListScreen(viewModel,
                navigateBack = { navController.popBackStack() },
                toCategoryCreation = { navController.navigate(CategoryCreationNav) },
                toCategoryEdit = { id -> navController.navigate(CategoryEditNav(id)) })
        }
        composable<PartyCreationNav> { backStackEntry ->
            val homeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HomeNav)
            }
            val viewModel = hiltViewModel<MainViewModel>(homeEntry)
            PartyCreationScreen(
                viewModel,
                navigateBack = { navController.popBackStack() },
                toPartyList = {
                    navController.navigate(PartyListNav) {
                        popUpTo(HomeNav)
                    }
                })
        }
        composable<PartyEditNav> { backStackEntry ->
            val partyEdit = backStackEntry.toRoute<PartyEditNav>()
            val homeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HomeNav)
            }
            val viewModel = hiltViewModel<MainViewModel>(homeEntry)
            PartyEditScreen(viewModel,
                id = partyEdit.id,
                navigateBack = { navController.popBackStack() })
        }
        composable<PartyListNav> { backStackEntry ->
            val homeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HomeNav)
            }
            val viewModel = hiltViewModel<MainViewModel>(homeEntry)
            PartyListScreen(viewModel,
                navigateBack = { navController.popBackStack() },
                toPartyCreation = { navController.navigate(PartyCreationNav) },
                toPartyEdit = { uuid -> navController.navigate(PartyEditNav(uuid)) })
        }
    }
}

@Serializable
data object HomeNav

@Serializable
data object CategoryCreationNav

@Serializable
data class CategoryEditNav(val id: String)

@Serializable
data object CategoryListNav

@Serializable
data object PartyCreationNav

@Serializable
data class PartyEditNav(val id: String)

@Serializable
data object PartyListNav
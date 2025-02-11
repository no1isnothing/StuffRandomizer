package com.thebipolaroptimist.stuffrandomizer

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryCreationScreen
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryEditScreen
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryListScreen
import com.thebipolaroptimist.stuffrandomizer.ui.HomeScreen
import com.thebipolaroptimist.stuffrandomizer.ui.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.ui.PartyCreationScreen
import com.thebipolaroptimist.stuffrandomizer.ui.PartyEditScreen
import com.thebipolaroptimist.stuffrandomizer.ui.PartyListScreen
import dagger.hilt.android.HiltAndroidApp
import kotlinx.serialization.Serializable
import java.util.UUID

@HiltAndroidApp
class StuffRandomizerApplication : Application()

@Composable
fun StuffRandomizerApp() {
    val navController = rememberNavController()
    StuffRandomizerNavHost(navController = navController)
}

@Composable
fun StuffRandomizerNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = HomeNav) {
        composable<HomeNav> {
            HomeScreen(
                toPartyCreation = { navController.navigate(PartyCreationNav) },
                toCategoryList = { navController.navigate(CategoryListNav) },
                toPartyList = { navController.navigate(PartyListNav) })
        }
        composable<CategoryCreationNav> {
                backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HomeNav)
            }
            val parentViewModel = hiltViewModel<MainViewModel>(parentEntry)
            CategoryCreationScreen(parentViewModel,
                toCategoryList = { navController.navigate(CategoryListNav) }
            )
        }
        composable<CategoryEditNav> {
            backStackEntry ->
            val categoryEdit = backStackEntry.toRoute<CategoryEditNav>()
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HomeNav)
            }
            val parentViewModel = hiltViewModel<MainViewModel>(parentEntry)
            CategoryEditScreen(parentViewModel, id = categoryEdit.id)

        }
        composable<CategoryListNav> {
                backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HomeNav)
            }
            val parentViewModel = hiltViewModel<MainViewModel>(parentEntry)
            CategoryListScreen(parentViewModel, toCategoryCreation = { navController.navigate(CategoryCreationNav) },
                toCategoryEdit = { id -> navController.navigate(CategoryEditNav(id))})
        }
        composable<PartyCreationNav> {
                backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HomeNav)
            }
            val parentViewModel = hiltViewModel<MainViewModel>(parentEntry)
            PartyCreationScreen(parentViewModel, toPartyList = { navController.navigate(PartyListNav)})
        }
        composable<PartyEditNav> {
            backStackEntry ->
            val partyEdit = backStackEntry.toRoute<PartyEditNav>()
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HomeNav)
            }
            val parentViewModel = hiltViewModel<MainViewModel>(parentEntry)
            PartyEditScreen(parentViewModel,id = partyEdit.id)
        }
        composable<PartyListNav> {
                backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(HomeNav)
            }
            val parentViewModel = hiltViewModel<MainViewModel>(parentEntry)
            PartyListScreen(parentViewModel, toPartyCreation = { navController.navigate(PartyCreationNav) },
                toPartyEdit = { uuid ->  navController.navigate(PartyEditNav(uuid)) })
        }
    }
}

@Serializable data object HomeNav
@Serializable data object CategoryCreationNav
@Serializable data class CategoryEditNav(val id: String)
@Serializable data object CategoryListNav
@Serializable data object PartyCreationNav
@Serializable data class PartyEditNav(val id: String)
@Serializable data object PartyListNav
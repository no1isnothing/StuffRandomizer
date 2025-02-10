package com.thebipolaroptimist.stuffrandomizer

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryCreationScreen
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryEditScreen
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryListScreen
import com.thebipolaroptimist.stuffrandomizer.ui.HomeScreen
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
            HomeScreen(toPartyCreation = { navController.navigate(PartyCreationNav) },
                toCategoryList = { navController.navigate(CategoryListNav) },
                toPartyList = { navController.navigate(PartyListNav) })
        }
        composable<CategoryCreationNav> {
            CategoryCreationScreen(
                toCategoryList = { navController.navigate(CategoryListNav) }
            )
        }
        composable<CategoryEditNav> {
            backStackEntry ->
            val categoryEdit = backStackEntry.toRoute<CategoryEditNav>()
            CategoryEditScreen(id = categoryEdit.id)

        }
        composable<CategoryListNav> {
            CategoryListScreen(toCategoryCreation = { navController.navigate(CategoryCreationNav) },
                toCategoryEdit = { id -> navController.navigate(CategoryEditNav(id))})
        }
        composable<PartyCreationNav> {
            PartyCreationScreen( toPartyList = { navController.navigate(PartyListNav)})
        }
        composable<PartyEditNav> {
            backStackEntry ->
            val partyEdit = backStackEntry.toRoute<PartyEditNav>()
            PartyEditScreen(id = partyEdit.id)
        }
        composable<PartyListNav> {
            PartyListScreen(toPartyCreation = { navController.navigate(PartyCreationNav) },
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
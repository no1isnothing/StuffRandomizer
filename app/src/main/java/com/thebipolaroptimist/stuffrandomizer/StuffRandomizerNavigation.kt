package com.thebipolaroptimist.stuffrandomizer

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryDetailsScreen
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryListScreen
import com.thebipolaroptimist.stuffrandomizer.ui.HomeScreen
import com.thebipolaroptimist.stuffrandomizer.ui.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.ui.PartyCreationScreen
import com.thebipolaroptimist.stuffrandomizer.ui.PartyEditScreen
import com.thebipolaroptimist.stuffrandomizer.ui.PartyListScreen
import kotlinx.serialization.Serializable

data class TopLevelRoute<T : Any>(val name: String, val route: T , val icon: ImageVector)
val topLevelRoutes = listOf(
    TopLevelRoute("Home", HomeNav, Icons.Default.Home),
    TopLevelRoute("Categories", CategoryListNav, Icons.Default.Menu),
    TopLevelRoute("Matches", PartyListNav, Icons.Default.Menu)
)

@Composable
fun StuffRandomizerNavHost(
    navController: NavHostController
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                topLevelRoutes.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.name
                            )
                        },
                        label = { Text(item.name) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index
                        navController.navigate(item.route)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = HomeNav, modifier = Modifier.padding(innerPadding)) {
            composable<HomeNav> {
                //TODO #16: Make sure this works without the backstack entry
                HomeScreen()
            }
            composable<CategoryDetailsNav> { backStackEntry ->
                val categoryEdit = backStackEntry.toRoute<CategoryDetailsNav>()
                val homeEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(HomeNav)
                }
                val viewModel = hiltViewModel<MainViewModel>(homeEntry)
                CategoryDetailsScreen(
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
                    toCategoryCreation = { navController.navigate(CategoryDetailsNav(null)) },
                    toCategoryEdit = { id -> navController.navigate(CategoryDetailsNav(id)) })
            }
            composable<PartyCreationNav> { backStackEntry ->
                val homeEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(HomeNav)
                }
                val viewModel = hiltViewModel<MainViewModel>(homeEntry)
                PartyCreationScreen(
                    viewModel,
                    navigateBack = { navController.popBackStack() },
                    toPartyEdit = { uuid ->
                        navController.navigate(PartyEditNav(uuid)) {
                            popUpTo(PartyListNav)
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
                    toPartyCreation = { navController.navigate(PartyCreationNav) },
                    toPartyEdit = { uuid -> navController.navigate(PartyEditNav(uuid)) })
            }
        }
        }
    }

@Serializable
data object HomeNav

@Serializable
data class CategoryDetailsNav(val id: String?)

@Serializable
data object CategoryListNav

@Serializable
data object PartyCreationNav

@Serializable
data class PartyEditNav(val id: String)

@Serializable
data object PartyListNav
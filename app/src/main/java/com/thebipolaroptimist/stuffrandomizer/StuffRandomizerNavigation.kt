package com.thebipolaroptimist.stuffrandomizer

import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryDetailsScreen
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryListScreen
import com.thebipolaroptimist.stuffrandomizer.ui.HomeScreen
import com.thebipolaroptimist.stuffrandomizer.ui.PartyCreationScreen
import com.thebipolaroptimist.stuffrandomizer.ui.PartyEditScreen
import com.thebipolaroptimist.stuffrandomizer.ui.PartyListScreen
import kotlinx.serialization.Serializable

data class TopLevelRoute<T : Any>(val nameId: Int, val route: T, val iconId: Int)

val topLevelRoutes = listOf(
    TopLevelRoute(R.string.home_label, HomeNav, R.drawable.home_24px),
    TopLevelRoute(R.string.category_label, CategoryListNav, R.drawable.category_24px),
    TopLevelRoute(R.string.matches_label, PartyListNav, R.drawable.lists_24px)
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
                                painter = painterResource(id = item.iconId),
                                contentDescription = stringResource(id = item.nameId)
                            )
                        },
                        label = { Text(stringResource(id = item.nameId)) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.route)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeNav,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<HomeNav> {
                //TODO #16: Make sure this works without the backstack entry
                HomeScreen()
            }
            composable<CategoryDetailsNav> { backStackEntry ->
                val categoryDetailsEntry = backStackEntry.toRoute<CategoryDetailsNav>()
                val homeEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(HomeNav)
                }
                val viewModel = hiltViewModel<MainViewModel>(homeEntry)
                CategoryDetailsScreen(
                    viewModel,
                    categoryId = categoryDetailsEntry.id,
                    onBack = { navController.popBackStack() })

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
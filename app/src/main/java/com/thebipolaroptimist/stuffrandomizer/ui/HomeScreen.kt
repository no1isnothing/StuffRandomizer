package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.ui.components.mainTopAppBarColors
import java.util.UUID

/**
 * A simple [Composable] as the default destination in the navigation.
 * Allows the user to launch [QuickSelectDialog].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel = hiltViewModel(),
) {
    val categoryList by viewModel.categories.observeAsState(listOf())
    val races = stringArrayResource(id = R.array.bg_race)
    val backgrounds = stringArrayResource(id = R.array.bg_background)
    val classes = stringArrayResource(id = R.array.bg_char_class)

    var menuExpanded by remember {
        mutableStateOf(false)
    }
    var showDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.home_label)) },
                actions = {
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.menu)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }) {
                        // TODO #7: Remove, hide behind flag, or update to be 'default data'
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.action_add_data)) },
                            onClick = {
                                viewModel.insertCategory(
                                    Category(
                                        UUID.randomUUID(),
                                        "BG Backgrounds",
                                        backgrounds.toList()
                                    )
                                )
                                viewModel.insertCategory(
                                    Category(
                                        UUID.randomUUID(),
                                        "BG Races",
                                        races.toList()
                                    )
                                )
                                viewModel.insertCategory(
                                    Category(
                                        UUID.randomUUID(),
                                        "BG Classes",
                                        classes.toList()
                                    )
                                )
                                addSampleData(viewModel)
                            })
                        // TODO #7: Remove, hide behind flag, or move to advanced settings with a warning
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.action_clear_data)) },
                            onClick = { clearAllData(viewModel) })
                    }
                },
                colors = mainTopAppBarColors()
            )
        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Button(onClick = { showDialog = true }) {
                Text(text = stringResource(R.string.quick_select))
            }
            if (showDialog) {
                QuickSelectDialog(
                    onDismiss = { showDialog = false },
                    categoryList = categoryList
                )
            }
        }
    }
}


private fun addSampleData(mainViewModel: MainViewModel) {
    mainViewModel.insertParty(createSamplePartyData())
    mainViewModel.insertCategories(createSampleCategoryData())
}

private fun clearAllData(mainViewModel: MainViewModel) {
    mainViewModel.deleteAllCategories()
    mainViewModel.deleteAllParties()
}

private fun createSamplePartyData(): Party = Party(
    UUID.randomUUID(),
    "Skyrim 2024",
    arrayListOf(
        Member("Jane", hashMapOf(Pair("Aedra", "Talos"), Pair("Daedra", "Clavicus Vile"))),
        Member("Bear", hashMapOf(Pair("Aedra", "Mara"), Pair("Daedra", "Peryite"))),
        Member("The Tooth Fairy", hashMapOf(Pair("Aedra", "Julianos"), Pair("Daedra", "Vaermina"))),
        Member("Gifty", hashMapOf(Pair("Aedra", "Stendar"), Pair("Daedra", "Merida"))),
    ),
    "Friends"
)

private fun createSampleCategoryData(): List<Category> = listOf(
    Category(
        UUID.randomUUID(),
        "TES Aedra",
        listOf(
            "Talos",
            "Julianos",
            "Arkay",
            "Akatosh",
            "Mara",
            "Stendarr",
            "Dibella",
            "Kynareth",
            "Zenithar"
        )
    ),
    Category(
        UUID.randomUUID(),
        "TES Daedra",
        listOf(
            "Clavicus Vile",
            "Meridia",
            "Peryite",
            "Azura",
            "Molag Bal",
            "Hermaus Mora",
            "Vaermina",
            "Nocturnal"
        )
    ),
    Category(
        UUID.randomUUID(),
        "Friends",
        listOf(
            "Jane",
            "Bear",
            "Gift",
            "The Tooth Fairy"
        )
    )
)
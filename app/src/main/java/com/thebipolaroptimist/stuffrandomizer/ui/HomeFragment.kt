package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import java.util.UUID

/**
 * A simple [Composable] as the default destination in the navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    toPartyCreation: () -> Unit = {},
    toPartyList: () -> Unit = {},
    toCategoryList: () -> Unit = {},
) {
    val parties by mainViewModel.parties.observeAsState(listOf())
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { toPartyCreation() },
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_match))
            }
        },
        topBar = {
            TopAppBar(title = { stringResource(id = R.string.home_fragment_label) },
                actions = {
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        // TODO #7: Remove, hide behind flag, or update to be 'default data'
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.action_add_data)) },
                            onClick = { addSampleData(mainViewModel) })
                        // TODO #7: Remove, hide behind flag, or move to advanced settings with a warning
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.action_clear_data)) },
                            onClick = { clearAllData(mainViewModel) })
                    }
                }
            )
        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .weight(1f),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (parties.isNotEmpty()) {
                        Text(
                            modifier = Modifier
                                .padding(16.dp),
                            text = parties[0].partyName
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { toPartyList() },
                        )
                        {
                            Text(text = stringResource(R.string.matches))
                        }
                    }
                }
            }
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .weight(1f),
            ) {
                Button(
                    onClick = { toCategoryList() },
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.Center)
                        .align(alignment = Alignment.CenterHorizontally),
                ) {
                    Text(text = stringResource(R.string.items))
                }
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
        "Aedra",
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
        "Daedra",
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
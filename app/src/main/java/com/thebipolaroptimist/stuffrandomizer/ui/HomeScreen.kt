package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.ui.components.DropDownText
import com.thebipolaroptimist.stuffrandomizer.ui.components.EditableSingleLineItem
import com.thebipolaroptimist.stuffrandomizer.ui.components.mainTopAppBarColors
import java.util.UUID

/**
 * A simple [Composable] as the default destination in the navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val categoryList by mainViewModel.categories.observeAsState(listOf())
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
            TopAppBar(title = { Text(stringResource(id = R.string.home_label)) },
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
                            onClick = {
                                mainViewModel.insertCategory(Category(UUID.randomUUID(), "BG Backgrounds", backgrounds.toList()))
                                mainViewModel.insertCategory(Category(UUID.randomUUID(), "BG Races", races.toList()))
                                mainViewModel.insertCategory(Category(UUID.randomUUID(), "BG Classes", classes.toList()))
                                addSampleData(mainViewModel)
                            })
                        // TODO #7: Remove, hide behind flag, or move to advanced settings with a warning
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.action_clear_data)) },
                            onClick = { clearAllData(mainViewModel) })
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
        ) {
            Button(onClick = { showDialog = true}) {
                Text(text = "Show dialog")
            }
            if(showDialog) {
                QuickSelectDialog(onDismiss = {showDialog = false },
                    categoryList = categoryList)
            }
        }
    }
}

enum class QuickSelectState {
    LIST_CREATE,
    LIST_SELECT,
    SHOW_SELECTED,
    DEFAULT,
}

@Composable
fun QuickSelectDialog(onDismiss: () -> Unit = {},
                      categoryList: List<Category>) {
    var newList = remember {
        mutableStateListOf("")
    }
    var selectedItem by remember {
        mutableStateOf("")
    }
    var selectState by remember {
        mutableStateOf(QuickSelectState.DEFAULT)
    }

    Dialog(
        onDismissRequest = { onDismiss() }) {
                when(selectState) {
                    QuickSelectState.LIST_CREATE -> {
                        CreateList(newList, {
                                item -> selectedItem = item
                            selectState = QuickSelectState.SHOW_SELECTED
                        })
                    }
                    QuickSelectState.LIST_SELECT -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(600.dp)
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Column(Modifier.align(Alignment.CenterHorizontally)) {
                                DropDownText(
                                    items = categoryList.map { it.name },
                                    onSelect = { index ->
                                        newList = categoryList[index].things.toMutableStateList()
                                        selectState = QuickSelectState.LIST_CREATE
                                    }
                                )
                            }
                            }
                    }
                    QuickSelectState.SHOW_SELECTED -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(600.dp)
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Column(Modifier.align(Alignment.CenterHorizontally)) {
                                Text(selectedItem)
                            }
                        }
                    }
                    QuickSelectState.DEFAULT -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(600.dp)
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Column(Modifier.align(Alignment.CenterHorizontally)) {
                                Button(onClick = { selectState = QuickSelectState.LIST_SELECT }) {
                                    Text(text = "Select from list")
                                }
                                Button(onClick = { selectState = QuickSelectState.LIST_CREATE }) {
                                    Text(text = "Enter new list")
                                }
                            }
                        }
                    }
                }
            }
      //  }
    //}
}

@Composable
private fun CreateList(
    newList: MutableList<String>,
    onSelect: (selected: String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(Modifier.align(Alignment.CenterHorizontally)) {
            LazyColumn(Modifier.weight(1f, fill = false)) {
                itemsIndexed(newList) { index, item ->
                    EditableSingleLineItem(
                        text = item,
                        remove = { newList.remove(it) },
                        update = { newList[index] = it }
                    )
                }
            }
            Row {
                Button(
                    modifier = Modifier.wrapContentSize(),
                    onClick = {
                        newList.add("")
                    }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add)
                    )
                }
                Button(
                    modifier = Modifier.wrapContentSize(),
                    onClick = {
                        onSelect(newList.random())
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.casino_24px),
                        contentDescription = stringResource(
                            id = R.string.roll
                        )
                    )
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
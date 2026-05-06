package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.ui.components.CheckableItem
import com.thebipolaroptimist.stuffrandomizer.ui.components.DropDownText
import com.thebipolaroptimist.stuffrandomizer.ui.components.LabelText
import com.thebipolaroptimist.stuffrandomizer.ui.components.mainOutlinedTextfieldColors
import com.thebipolaroptimist.stuffrandomizer.ui.components.mainTopAppBarColors
import kotlinx.coroutines.launch

/**
 * A [Composable] for creating [Party]s.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyCreationScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    navigateBack: () -> Unit = {},
    toPartyEdit: (uuid: String) -> Unit = {}
) {
    val categoryList by mainViewModel.categories.observeAsState(listOf())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val categoryNames by remember(categoryList) {
        derivedStateOf { categoryList.map { it.name } }
    }
    val newPartyName by mainViewModel.newPartyName.collectAsState()
    val newPartyCheckedState by mainViewModel.newPartyCheckedState.collectAsState()
    val newAssigneeSelection by mainViewModel.newAssigneeSelection.collectAsState()

    LaunchedEffect(categoryList.size) {  mainViewModel.setupNewPartyState(categoryList.size) }
    
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.party_creation_label)) },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        mainViewModel.attemptToCreateAndSaveParty(
                            categoryList = categoryList,
                            onSuccess = { partyUid -> toPartyEdit(partyUid) },
                            onNameEmpty = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(context.getString(R.string.warning_match_name_empty))
                                }
                            },
                            onAssignmentsEmpty = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(context.getString(R.string.warning_match_assignments_empty))
                                }
                            })}
                        ) {
                        Icon(painter = painterResource(id = R.drawable.casino_24px), contentDescription = stringResource(
                            id = R.string.roll
                        ))
                    }
                },
                colors = mainTopAppBarColors())
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .testTag("party_name_field"),
                value = newPartyName,
                onValueChange = { mainViewModel.updateNewPartyName(it) },
                label = { Text(stringResource(R.string.hint_match_name)) },
                colors = mainOutlinedTextfieldColors()
            )
            LabelText(
                text = stringResource(R.string.assignees),
            )
            DropDownText(
                modifier = Modifier.testTag("assignee_dropdown"),
                items = categoryNames,
                selectedIndex = newAssigneeSelection,
                onSelect = { index -> mainViewModel.updateNewAssigneeSelection(index)})
            LabelText(
                text = stringResource(R.string.assignments),
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .testTag("assignments_list")
            ) {
                itemsIndexed(
                    items =categoryList,
                    key = {_, category -> category.uid}
                ) { index, category ->
                    CheckableItem(
                        modifier = Modifier.testTag("assignment_item_${category.name}"),
                        name = category.name,
                        isChecked = newPartyCheckedState.getOrElse(index) { false },
                        onCheck = { checked ->
                            mainViewModel.updatePartyCheckedState(index, checked)
                        }
                    )
                }
            }
        }
    }
}
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    mainViewModel.setupNewPartyState(categoryList.size)

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
                        if (mainViewModel.newPartyName.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.warning_match_name_empty))
                            }
                            return@IconButton
                        }
                        val party = mainViewModel.createAndSaveNewParty(categoryList)
                        if (party != null) {
                            toPartyEdit(party.uid.toString())
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.warning_match_assignments_empty))
                            }
                        } }) {
                        Icon(painter = painterResource(id = R.drawable.casino_24px), contentDescription = stringResource(
                            id = R.string.roll
                        ))
                    }
                })
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                value = mainViewModel.newPartyName,
                onValueChange = { mainViewModel.newPartyName = it },
                label = { Text(stringResource(R.string.hint_match_name)) }
            )
            LabelText(
                text = stringResource(R.string.assignees),
            )
            DropDownText(
                items = categoryList.map { it.name },
                onSelect = { index -> mainViewModel.newAssigneeSelection = index})
            LabelText(
                text = stringResource(R.string.assignments),
            )
            LazyColumn(Modifier.weight(1f, fill = false)) {
                itemsIndexed(categoryList)
                { index, category ->
                    CheckableItem(
                        name = category.name,
                        onCheck = { checked -> mainViewModel.newPartyCheckedState[index] = checked }
                    )
                }
            }
        }
    }
}
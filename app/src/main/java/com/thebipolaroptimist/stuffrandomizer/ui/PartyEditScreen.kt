package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.ui.components.ClickableTwoLineItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyEditScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    id: String,
    navigateBack: () -> Unit = {},
) {
    val partyList by mainViewModel.parties.observeAsState(listOf())

    val party = partyList.firstOrNull { parties -> parties.uid.toString() == id }

    if (id != mainViewModel.editPartyUuid && party != null) {
        mainViewModel.loadEditParty(party)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.party_edit_label)) },
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
                        if (party != null) {
                            mainViewModel.rerollEditPartyMembers(party)
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.casino_24px),
                            contentDescription = stringResource(
                                id = R.string.roll
                            )
                        )
                    }
                    IconButton(onClick = {
                        if (party != null) {
                            mainViewModel.saveEditedParty(party)
                        }

                        navigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = stringResource(R.string.save)
                        )
                    }
                })
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                value = mainViewModel.editPartyName,
                onValueChange = { mainViewModel.editPartyName = it },
                label = { Text(stringResource(R.string.hint_match_name)) }
            )
            LazyColumn(
                Modifier
                    .weight(1f)
            ) {
                items(mainViewModel.editPartyMembers) { item ->
                    ClickableTwoLineItem(item.assignee,
                        item.assignments
                            .map { assignment -> assignment.key + ": " + assignment.value }
                            .joinToString()
                    )
                }
            }
        }
    }

}
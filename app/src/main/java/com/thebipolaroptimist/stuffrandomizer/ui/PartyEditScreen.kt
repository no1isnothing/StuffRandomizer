package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Member

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
                })
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            TextField(value = mainViewModel.editPartyName,
                onValueChange = { mainViewModel.editPartyName = it },
                label = { Text(stringResource(R.string.hint_match_name)) }
            )
            LazyColumn(
                Modifier
                    .weight(1f)
            ) {
                items(mainViewModel.editPartyMembers) { item ->
                    MemberEditItem(member = item)
                }
            }
            Button(onClick = {
                if (party != null) {
                    mainViewModel.rerollEditPartyMembers(party)
                }
            }) {
                Text(text = stringResource(R.string.reroll))
            }
            Button(onClick = {
                if (party != null) {
                    mainViewModel.saveEditedParty(party)
                }

                navigateBack()
            }) {
                Text(text = stringResource(R.string.save))
            }
        }
    }

}

@Composable
fun MemberEditItem(member: Member) {
    Column {
        Text(member.assignee)
        Text(
            member.assignments
                .map { assignment -> assignment.key + ": " + assignment.value }.joinToString()
        )
    }
}
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.utilties.Parties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


private fun reroll(mainViewModel: MainViewModel, scope: CoroutineScope, party: Party?) {
    party?.let {
        scope.launch {
            val categoryNames = it.getAllCategoryNames()

            val categories = mainViewModel.getCategoriesByName(categoryNames)
            val assignees = mainViewModel.getCategoryByName(it.assigneeList)

            if (assignees == null) {
                partyEditLogger.atWarning().log("Category %s not found", it.assigneeList)
                return@launch
            }

            mainViewModel.editPartyMembers.clear()
            mainViewModel.editPartyMembers.addAll(Parties.roll(assignees.things, categories))
        }
    }
}

val partyEditLogger: FluentLogger = FluentLogger.forEnclosingClass()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyEditScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    id: String,
    navigateBack: () -> Unit = {},
    toPartyList: () -> Unit = {}
) {
    val partyList by mainViewModel.parties.observeAsState(listOf())
    val coroutineScope = rememberCoroutineScope()
    val party = partyList.firstOrNull { parties -> parties.uid.toString() == id }
    if (id != mainViewModel.editPartyUuid) {
        partyEditLogger.atInfo().log("Loading information for party %s", party)
        party?.let {
            mainViewModel.editPartyMembers.clear()
            mainViewModel.editPartyMembers.addAll(it.members)
            mainViewModel.editPartyName = it.partyName
            mainViewModel.editPartyUuid = it.uid.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.party_edit_fragment_label)) },
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
            Button(onClick = { reroll(mainViewModel, coroutineScope, party) }) {
                Text(text = stringResource(R.string.reroll))
            }
            Button(onClick = {
                party?.let {
                    it.partyName = mainViewModel.editPartyName
                    it.members.clear()
                    it.members.addAll(mainViewModel.editPartyMembers)

                    partyEditLogger.atInfo().log("Inserting party %s", it)
                    mainViewModel.insertParty(it)

                    mainViewModel.editPartyMembers.clear()
                    mainViewModel.editPartyName = ""
                    mainViewModel.editPartyUuid = ""
                }

                toPartyList()
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
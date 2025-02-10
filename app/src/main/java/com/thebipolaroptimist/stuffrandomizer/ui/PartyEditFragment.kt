package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryEditFragment.Companion.TEMP_UUID
import com.thebipolaroptimist.stuffrandomizer.utilties.Parties
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

//private val logger: FluentLogger = FluentLogger.forEnclosingClass()

private fun reroll(mainViewModel: MainViewModel, scope: CoroutineScope, party: Party?) {
    party?.let {
        scope.launch {
            val categoryNames = it.getAllCategoryNames()

            val categories = mainViewModel.getCategoriesByName(categoryNames)
            val assignees = mainViewModel.getCategoryByName(it.assigneeList)

            if (assignees == null) {
                logger.atWarning().log("Category %s not found", it.assigneeList)
                return@launch
            }

            mainViewModel.editPartyMembers.clear()
            mainViewModel.editPartyMembers.addAll(Parties.roll(assignees.things, categories))
        }
    }
}

@Composable
fun PartyEditScreen(mainViewModel: MainViewModel = hiltViewModel(),
                    id: String,
                    toPartyList: () -> Unit = {}) {
    var party = mainViewModel.parties.value
        ?.first { parties -> parties.uid == UUID.fromString(id) }
    val coroutineScope = rememberCoroutineScope()

    if(id != mainViewModel.editPartyUuid) {
        party?.let {
            mainViewModel.editPartyMembers.clear()
            mainViewModel.editPartyMembers.addAll(it.members)
            mainViewModel.editPartyName = it.partyName
            mainViewModel.editPartyUuid = it.uid.toString()
        }
    }

    Column {
        TextField(value = mainViewModel.editPartyName,
            onValueChange = { mainViewModel.editPartyName = it },
            label = { Text(stringResource(R.string.hint_match_name)) }
        )
        LazyColumn(Modifier.weight(1f)) {
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

                logger.atInfo().log("Inserting party %s", it)
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

@Composable
fun MemberEditItem(member: Member) {
    Column {
        Text(member.assignee)
        Text(member.assignments
            .map { assignment -> assignment.key + ": " + assignment.value }.joinToString()
        )
    }
}
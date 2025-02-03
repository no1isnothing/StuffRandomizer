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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.ui.CategoryEditFragment.Companion.TEMP_UUID
import com.thebipolaroptimist.stuffrandomizer.utilties.Parties
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID


/**
 * A [Fragment] for editing a [Party].
 */
@AndroidEntryPoint
class PartyEditFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()

    private var partyUuid = TEMP_UUID
    var party: Party? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val uuidString = arguments?.getString(resources.getString(R.string.key_uuid))
        partyUuid = UUID.fromString(uuidString)

        party = mainViewModel.parties.value
            ?.first { parties -> parties.uid == partyUuid }

        if(uuidString == mainViewModel.editPartyUuid) {
            return ComposeView(requireContext()).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    PartyEditScreen()
                }
            }
        }

        party?.let {
            logger.atInfo().log("Members size " + it.members.size)
            mainViewModel.editPartyMembers.clear()
            mainViewModel.editPartyMembers.addAll(it.members)
            mainViewModel.editPartyName = it.partyName
            mainViewModel.editPartyUuid = it.uid.toString()
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                PartyEditScreen()
            }
        }
    }


    private fun reroll() {
        party?.let {
            lifecycleScope.launch {
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
    fun PartyEditScreen() {
        Column {
            TextField(value = mainViewModel.editPartyName,
                onValueChange = { mainViewModel.editPartyName = it },
                label = { Text(resources.getString(R.string.hint_match_name)) }
            )
            LazyColumn(Modifier.weight(1f)) {
                items(mainViewModel.editPartyMembers) { item ->
                    MemberEditItem(member = item)
                }
            }
            Button(onClick = { reroll() }) {
                Text(text = resources.getString(R.string.reroll))
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

                findNavController().navigate(R.id.action_PartyEditFragment_to_PartyListFragment)
            }) {
                Text(text = resources.getString(R.string.save))
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

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}
package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.databinding.FragmentPartyListBinding
import com.google.common.flogger.FluentLogger
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

/**
 * A [Composable] for displaying [Party]s.
 */
@Composable
fun PartyListScreen(mainViewModel: MainViewModel = hiltViewModel(),
                    toPartyCreation: () -> Unit = {},
                    toPartyEdit: (uuid: String) -> Unit) {
    val partyList by mainViewModel.parties.observeAsState(listOf())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { toPartyCreation() },
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_match))
            }
        }) {
            padding ->
        LazyColumn(Modifier.fillMaxSize()
            .padding(padding)) {
            items(partyList) { item ->
                PartyItem(item, toPartyEdit)
            }
        }
    }
}

@Composable
fun PartyItem(party: Party, toPartyEdit: (uuid: String) -> Unit) {
    logger.atInfo().log("Creating Party %s", party.partyName)
    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                toPartyEdit(party.uid.toString())
            }) {
        Column {
            Text(party.partyName)
            Text(party.members.joinToString { member -> member.assignee })
        }
    }
}

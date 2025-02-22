package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Party

/**
 * A [Composable] for displaying [Party]s.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyListScreen(mainViewModel: MainViewModel = hiltViewModel(),
                    navigateBack: ()-> Unit = {},
                    toPartyCreation: () -> Unit = {},
                    toPartyEdit: (uuid: String) -> Unit) {
    val partyList by mainViewModel.parties.observeAsState(listOf())

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(id = R.string.party_list_fragment_label)) },
            navigationIcon = {
                IconButton(onClick = { navigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }) },
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

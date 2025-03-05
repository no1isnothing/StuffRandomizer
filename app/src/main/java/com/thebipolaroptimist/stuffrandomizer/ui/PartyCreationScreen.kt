package com.thebipolaroptimist.stuffrandomizer.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Party

// navigate to match edit after roll
// show preview of lists for selection?

/**
 * A [Composable] for creating [Party]s.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyCreationScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    navigateBack: () -> Unit = {},
    toPartyList: () -> Unit = {}
) {
    val categoryList by mainViewModel.categories.observeAsState(listOf())
    val context = LocalContext.current

    // look into a better way to do this
    if (mainViewModel.newPartyCheckedSate.size == 0) {
        mainViewModel.newPartyCheckedSate.addAll(Array(categoryList.size) { false })
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.party_creation_label)) },
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
            LazyColumn(Modifier.weight(1f)) {
                itemsIndexed(categoryList)
                { index, category ->
                    CheckableItem(
                        categoryName = category.name,
                        // change to function in view model
                        onCheck = { checked -> mainViewModel.newPartyCheckedSate[index] = checked }
                    )
                }
            }
            Button(onClick = {
                if (mainViewModel.newPartyName.isEmpty()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.warning_match_name_empty),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@Button
                }

                if (mainViewModel.createAndSaveNewParty(categoryList)) {
                    toPartyList()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.warning_match_assignments_empty),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }) {
                Text(stringResource(R.string.roll))
            }
        }
    }
}
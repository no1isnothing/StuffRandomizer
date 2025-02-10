package com.thebipolaroptimist.stuffrandomizer.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.fragment.findNavController
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.utilties.Parties
import dagger.hilt.android.AndroidEntryPoint


private fun createParty(categoryList: List<Category>, mainViewModel: MainViewModel, context: Context): Boolean {
    if (mainViewModel.newPartyName.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.warning_match_name_empty),
            Toast.LENGTH_SHORT
        )
            .show()
        return false
    }

    val selectedCategoryList = ArrayList<Category>()
    val assigneeList = categoryList[mainViewModel.newAssigneeSelection]

    for ((index, checkBoxState) in mainViewModel.newPartyCheckedSate.withIndex()) {
        if (checkBoxState) {
            selectedCategoryList.add(categoryList[index])
        }
    }
    if (selectedCategoryList.isEmpty()) {
        Toast.makeText(
            context,
            context.getString(R.string.warning_match_assignments_empty),
            Toast.LENGTH_SHORT
        )
            .show()
        return false
    }

    val party = Parties.create(mainViewModel.newPartyName, assigneeList, selectedCategoryList)
    mainViewModel.insertParty(party)
    return true
}


@Composable
fun PartyCreationScreen(mainViewModel: MainViewModel = hiltViewModel(),
                                toPartyList: () -> Unit = {}) {
    val categoryList by mainViewModel.categories.observeAsState(listOf())
    val context = LocalContext.current

    // look into a better way to do this
    if (mainViewModel.newPartyCheckedSate.size == 0) {
        mainViewModel.newPartyCheckedSate.addAll(Array(categoryList.size) { false })
    }

    Column {
        TextField(
            value = mainViewModel.newPartyName,
            onValueChange = { mainViewModel.newPartyName = it },
            label = { Text(stringResource(R.string.hint_match_name)) }
        )
        Text(stringResource(R.string.assignees))
        DropDownText(categoryList, mainViewModel)
        Text(stringResource(R.string.assignments))
        LazyColumn(Modifier.weight(1f)) {
            itemsIndexed(categoryList)
            { index, category ->
                CategorySelectItem(
                    index = index,
                    categoryName = category.name,
                    mainViewModel
                )
            }
        }
        Button(onClick = {
            if (createParty(categoryList, mainViewModel, context)) {
                mainViewModel.clearNewParty()
                toPartyList()
            }
        }) {
            Text(stringResource(R.string.roll))
        }
    }

}

@Composable
private fun CategorySelectItem(index: Int, categoryName: String, mainViewModel: MainViewModel) {
    Row() {
        Checkbox(
            checked = mainViewModel.newPartyCheckedSate[index],
            onCheckedChange = { isChecked ->
                mainViewModel.newPartyCheckedSate[index] = isChecked
            }
        )
        Text(categoryName)
    }
}


@Composable
private fun DropDownText(categories: List<Category>, mainViewModel: MainViewModel) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box(Modifier.wrapContentSize()) {
        Row(Modifier.clickable { expanded = true }) {
            if (categories.size > mainViewModel.newAssigneeSelection) {
                Text(categories[mainViewModel.newAssigneeSelection].name)
            }
            Image(
                Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.expand)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }) {
            categories.forEachIndexed { index, category ->
                DropdownMenuItem(text = {
                    Text(text = category.name)
                },
                    onClick = {
                        expanded = false
                        mainViewModel.newAssigneeSelection = index
                    })
            }
        }

    }

}

/**
 * A [Fragment] for creating [Party]s.
 */
@AndroidEntryPoint
class PartyCreationFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent { PartyCreationScreen() }
        }
    }



    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}
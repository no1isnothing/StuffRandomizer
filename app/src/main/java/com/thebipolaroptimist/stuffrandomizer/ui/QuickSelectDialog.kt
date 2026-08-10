package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.ui.components.DropDownText
import com.thebipolaroptimist.stuffrandomizer.ui.components.EditableSingleLineItem
import com.thebipolaroptimist.stuffrandomizer.utilties.QuickSelect


enum class QuickSelectState {
    LIST_CREATE,
    LIST_SELECT,
    SHOW_SELECTED,
    DEFAULT,
}

/**
 * Holds the state for [QuickSelectDialog].
 *
 * A plain remembered state holder rather than a `ViewModel`: [QuickSelectDialog] is a [Dialog],
 * not a navigation destination, so a `hiltViewModel()` here would scope to the *enclosing*
 * screen's backstack entry and wrongly survive the dialog being dismissed and reopened.
 */
@Stable
class QuickSelectDialogState {
    var newList: SnapshotStateList<String> by mutableStateOf(mutableStateListOf(""))
        private set
    var selectedItem: String by mutableStateOf("")
        private set
    var selectState: QuickSelectState by mutableStateOf(QuickSelectState.DEFAULT)
        private set

    fun showListSelect() {
        selectState = QuickSelectState.LIST_SELECT
    }

    fun startNewList() {
        selectState = QuickSelectState.LIST_CREATE
    }

    fun selectExistingList(category: Category) {
        newList = category.things.toMutableStateList()
        selectState = QuickSelectState.LIST_CREATE
    }

    fun addBlankItem() {
        newList.add("")
    }

    fun updateItem(index: Int, value: String) {
        if (index in newList.indices) {
            newList[index] = value
        }
    }

    fun removeItem(value: String) {
        newList.remove(value)
    }

    /**
     * Pick a random item from [newList] and move to [QuickSelectState.SHOW_SELECTED]. Does
     * nothing if there's nothing to pick.
     */
    fun rollSelection() {
        QuickSelect.pickRandom(newList)?.let {
            selectedItem = it
            selectState = QuickSelectState.SHOW_SELECTED
        }
    }
}

@Composable
fun rememberQuickSelectDialogState(): QuickSelectDialogState = remember { QuickSelectDialogState() }

@Preview
@Composable
fun QuickSelectDialogPreview()
{
    QuickSelectDialog(categoryList = listOf(
        Category(name = "1", things = listOf("1", "2", "3")),
        Category(name = "2", things = listOf("1", "2", "3")),
        Category(name = "3", things = listOf("1", "2", "3"))))
}

@Composable
fun Modifier.quickSelectDialogModifier() : Modifier {
    val description = stringResource(R.string.quick_select_dialog)
    return this
        .fillMaxWidth()
        .padding(dimensionResource(R.dimen.padding_medium))
        .semantics { contentDescription = description }
}

/**
 * A dialog for quickly selecting something from a list.
 * @param onDismiss function to call on dismiss
 * @param categoryList The list of things to select from
 */
@Composable
fun QuickSelectDialog(
    state: QuickSelectDialogState = rememberQuickSelectDialogState(),
    onDismiss: () -> Unit = {},
    categoryList: List<Category> = listOf()
) {
    Dialog(
        onDismissRequest = { onDismiss() }) {
        when (state.selectState) {
            QuickSelectState.LIST_CREATE -> {
                CreateList(state)
            }

            QuickSelectState.LIST_SELECT -> {
                Card(
                    modifier = Modifier
                        .quickSelectDialogModifier()
                        .height(dimensionResource(R.dimen.dialog_height_small)),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
                ) {
                    Column(Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center) {
                        DropDownText(
                            items = categoryList.map { it.name },
                            onSelect = { index ->
                                state.selectExistingList(categoryList[index])
                            }
                        )
                    }
                }
            }

            QuickSelectState.SHOW_SELECTED -> {
                Card(
                    modifier = Modifier
                        .quickSelectDialogModifier()
                        .height(dimensionResource(R.dimen.dialog_height_small)),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
                ) {
                    Column(Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.selected))
                        Text(state.selectedItem,
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center)
                    }
                }
            }

            QuickSelectState.DEFAULT -> {
                Card(
                    modifier = Modifier
                        .quickSelectDialogModifier()
                        .height(dimensionResource(R.dimen.dialog_height_small)),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
                ) {
                    Column(
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(onClick = { state.showListSelect() }){
                            Text(text = stringResource(R.string.select_existing_list))
                        }
                        Button(onClick = { state.startNewList() }) {
                            Text(text = stringResource(R.string.enter_new_list))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateList(state: QuickSelectDialogState) {
    Card(
        modifier = Modifier
            .quickSelectDialogModifier()
            .height(dimensionResource(R.dimen.dialog_height_large)),
        shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
    ) {
        Column(Modifier
            .align(Alignment.CenterHorizontally)
            .padding(dimensionResource(R.dimen.padding_small))) {
            LazyColumn(Modifier.weight(1f, fill = false)) {
                itemsIndexed(state.newList) { index, item ->
                    EditableSingleLineItem(
                        text = item,
                        remove = { state.removeItem(it) },
                        update = { state.updateItem(index, it) }
                    )
                }
            }
            Row {
                Button(
                    modifier = Modifier.wrapContentSize(),
                    onClick = { state.addBlankItem() }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add)
                    )
                }
                Button(
                    modifier = Modifier.wrapContentSize(),
                    onClick = { state.rollSelection() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.casino_24px),
                        contentDescription = stringResource(
                            id = R.string.roll
                        )
                    )
                }
            }
        }
    }
}

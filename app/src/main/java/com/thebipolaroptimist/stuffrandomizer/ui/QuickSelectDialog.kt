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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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


enum class QuickSelectState {
    LIST_CREATE,
    LIST_SELECT,
    SHOW_SELECTED,
    DEFAULT,
}

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
    onDismiss: () -> Unit = {},
    categoryList: List<Category> = listOf()
) {
    var newList = remember {
        mutableStateListOf("")
    }
    var selectedItem by remember {
        mutableStateOf("")
    }
    var selectState by remember {
        mutableStateOf(QuickSelectState.DEFAULT)
    }

    Dialog(
        onDismissRequest = { onDismiss() }) {
        when (selectState) {
            QuickSelectState.LIST_CREATE -> {
                CreateList(newList, { item ->
                    selectedItem = item
                    selectState = QuickSelectState.SHOW_SELECTED
                })
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
                                newList = categoryList[index].things.toMutableStateList()
                                selectState = QuickSelectState.LIST_CREATE
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
                        Text(selectedItem,
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
                        Button(onClick = { selectState = QuickSelectState.LIST_SELECT }){
                            Text(text = stringResource(R.string.select_existing_list))
                        }
                        Button(onClick = { selectState = QuickSelectState.LIST_CREATE }) {
                            Text(text = stringResource(R.string.enter_new_list))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateList(
    newList: MutableList<String>,
    onSelect: (selected: String) -> Unit,
) {
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
                itemsIndexed(newList) { index, item ->
                    EditableSingleLineItem(
                        text = item,
                        remove = { newList.remove(it) },
                        update = { newList[index] = it }
                    )
                }
            }
            Row {
                Button(
                    modifier = Modifier.wrapContentSize(),
                    onClick = {
                        newList.add("")
                    }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add)
                    )
                }
                Button(
                    modifier = Modifier.wrapContentSize(),
                    onClick = {
                        onSelect(newList.random())
                    }) {
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
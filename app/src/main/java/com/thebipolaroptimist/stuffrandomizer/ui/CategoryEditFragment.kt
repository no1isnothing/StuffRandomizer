package com.thebipolaroptimist.stuffrandomizer.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import java.util.UUID


/**
 * A simple [Composable] to edit [Category]s.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    navigateBack: () -> Unit = {},
    id: String
) {
    val context = LocalContext.current
    val category =
        mainViewModel.categories
            .value?.first { category -> category.uid == UUID.fromString(id) }

    mainViewModel.editThings.clear()
    category?.let {
        mainViewModel.editThings.addAll(it.things)
        mainViewModel.editCategoryName = it.name
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.category_edit_fragment_label)) },
                navigationIcon = {
                    IconButton(onClick = {
                        category?.let {
                            it.things = mainViewModel.editThings
                            it.name = mainViewModel.editCategoryName
                            mainViewModel.insertCategory(it)
                        }
                        navigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                })
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            TextField(
                value = mainViewModel.editCategoryName,
                onValueChange = { mainViewModel.editCategoryName = it },
                label = { Text(stringResource(R.string.hint_list_name)) })
            LazyColumn(Modifier.weight(1f)) {
                itemsIndexed(mainViewModel.editThings,
                    key = { _, string -> string }) { index, item ->
                    CategoryEditItem(item = item, position = index, mainViewModel)
                }
            }
            Button(modifier = Modifier.wrapContentSize(),
                onClick = {
                    if (mainViewModel.editThings.contains("")) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.warning_duplicate_item),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        mainViewModel.editThings.add("")
                    }
                }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add)
                )
            }
            Button(modifier = Modifier.wrapContentSize(),
                onClick = {
                    mainViewModel.editThings.clear()
                    category?.let {
                        mainViewModel.editCategoryName = it.name
                        mainViewModel.editThings.addAll(it.things)
                    }
                }) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = stringResource(R.string.clear)
                )
            }
        }
    }
}

@Composable
fun CategoryEditItem(item: String, position: Int, mainViewModel: MainViewModel) {
    var editing by rememberSaveable {
        mutableStateOf(false)
    }
    var editedItem by rememberSaveable {
        mutableStateOf(item)
    }
    Row(Modifier.wrapContentSize()) {
        if (editing) {
            TextField(
                value = editedItem,
                onValueChange = { editedItem = it },
                label = { Text(stringResource(R.string.hint_list_name)) })
        } else {
            mainViewModel.editThings[position] = editedItem
            Text(editedItem)
        }
        Button(onClick = {
            editing = !editing
        }) {
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit)
            )
        }
        Button(onClick = {
            mainViewModel.editThings.remove(editedItem)
        }) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete)
            )
        }
    }
}
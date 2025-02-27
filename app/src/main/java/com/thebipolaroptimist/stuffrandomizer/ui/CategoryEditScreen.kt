package com.thebipolaroptimist.stuffrandomizer.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
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
            TopAppBar(title = { Text(stringResource(id = R.string.category_edit_label)) },
                navigationIcon = {
                    IconButton(onClick = {
                        category?.let {
                            if (mainViewModel.editThings.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.warning_empty_item_list),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                return@IconButton
                            }
                            it.things =
                                mainViewModel.editThings.filter { s -> s.isNotEmpty() }.toList()
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
                },
                actions = {
                    IconButton(onClick = {
                        mainViewModel.editThings.clear()
                        category?.let {
                            mainViewModel.editCategoryName = it.name
                            mainViewModel.editThings.addAll(it.things)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(id = R.string.refresh)
                        )
                    }
                }
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                value = mainViewModel.editCategoryName,
                onValueChange = { mainViewModel.editCategoryName = it },
                label = { Text(stringResource(R.string.hint_list_name)) })
            Text(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                text = stringResource(id = R.string.items),
                style = MaterialTheme.typography.titleMedium
            )
            LazyColumn(Modifier.weight(1f, fill = false)) {
                itemsIndexed(mainViewModel.editThings) { index, item ->
                    EditableSingleLineItem(text = item, position = index,
                        update = { position, text ->
                            mainViewModel.updateEditThings(
                                position,
                                text
                            )
                        },
                        remove = { text -> mainViewModel.editThings.remove(text) }
                    )
                }
            }
            Button(modifier = Modifier.wrapContentSize(),
                onClick = {
                    mainViewModel.editThings.add("")
                }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add)
                )
            }
        }
    }
}
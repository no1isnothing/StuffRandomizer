package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.ui.components.EditableSingleLineItem
import com.thebipolaroptimist.stuffrandomizer.ui.components.LabelText
import com.thebipolaroptimist.stuffrandomizer.ui.components.mainOutlinedTextfieldColors
import com.thebipolaroptimist.stuffrandomizer.ui.components.mainTopAppBarColors
import kotlinx.coroutines.launch
import java.util.UUID


/**
 * A simple [Composable] to create/edit [Category]s.
 *
 * @param viewModel The ViewModel to use for interacting with data.
 * @param onBack A function to handle back navigation.
 * @param categoryId The UUID for the [Category] to edit, if null create a new category.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailsScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    categoryId: String?
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    viewModel.fetchCategories()

    val currentCategory = categoryId?.let {
        viewModel.categories
            .value?.first { category -> category.uid == UUID.fromString(categoryId) }
    } ?: Category(UUID.randomUUID(), "", listOf())

    viewModel.setupCurrentCategory(currentCategory)

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarState)
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.category_details_label)) },
                navigationIcon = {
                    IconButton(onClick = {
                        currentCategory.let {
                            if (viewModel.currentCategoryName.isEmpty() && viewModel.currentCategoryThings.isEmpty()) {
                                coroutineScope.launch {
                                    snackbarState.showSnackbar(context.getString(R.string.removing_empty_category))
                                }

                                viewModel.deleteCategory(currentCategory)
                                onBack()
                                return@IconButton
                            }

                            if (viewModel.currentCategoryThings.isEmpty()) {
                                coroutineScope.launch {
                                    snackbarState.showSnackbar(context.getString(R.string.warning_empty_item_list))
                                }
                                return@IconButton
                            }

                            if (viewModel.currentCategoryName.isEmpty()) {
                                coroutineScope.launch {
                                    snackbarState.showSnackbar(context.getString(R.string.warning_list_name_empty))
                                }
                                return@IconButton
                            }

                            it.things =
                                viewModel.currentCategoryThings.filter { thing -> thing.isNotEmpty() }
                                    .toList()
                            it.name = viewModel.currentCategoryName
                            viewModel.insertCategory(it)
                        }
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.currentCategoryThings.clear()
                        currentCategory.let {
                            viewModel.currentCategoryName = it.name
                            viewModel.currentCategoryThings.addAll(it.things)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(id = R.string.refresh)
                        )
                    }
                    IconButton(onClick = {
                        viewModel.resetEditedParty()
                        viewModel.deleteCategory(currentCategory)
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete)
                        )
                    }
                },
                colors = mainTopAppBarColors()
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                value = viewModel.currentCategoryName,
                onValueChange = { viewModel.currentCategoryName = it },
                label = {
                    Text(
                        stringResource(R.string.hint_list_name),
                    )
                },
                colors = mainOutlinedTextfieldColors()
            )
            LabelText(text = stringResource(id = R.string.items))
            LazyColumn(Modifier.weight(1f, fill = false).semantics { contentDescription = "items" }) {
                itemsIndexed(viewModel.currentCategoryThings) { index, item ->
                    key(item) {
                        EditableSingleLineItem(text = item,
                            update = { text ->
                                viewModel.updateCurrentCategoryThings(
                                    index,
                                    text
                                )
                            },
                            remove = { text -> viewModel.currentCategoryThings.remove(text) }
                        )
                    }
                }
            }
            Button(modifier = Modifier.wrapContentSize(),
                onClick = {
                    viewModel.currentCategoryThings.add("")
                }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add)
                )
            }
        }
    }
}
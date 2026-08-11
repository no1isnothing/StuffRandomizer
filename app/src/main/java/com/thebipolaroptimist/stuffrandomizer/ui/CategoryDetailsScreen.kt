package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.ui.components.EditableSingleLineItem
import com.thebipolaroptimist.stuffrandomizer.ui.components.EmptyStateScreen
import com.thebipolaroptimist.stuffrandomizer.ui.components.LabelText
import com.thebipolaroptimist.stuffrandomizer.ui.components.mainOutlinedTextfieldColors
import com.thebipolaroptimist.stuffrandomizer.ui.components.mainTopAppBarColors
import com.thebipolaroptimist.stuffrandomizer.viewmodel.CategoryDetailsViewModel
import kotlinx.coroutines.launch


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
    viewModel: CategoryDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    categoryId: String?
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(categoryId) {
        viewModel.load(categoryId)
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(id = R.string.delete_category_title)) },
            text = { Text(stringResource(id = R.string.delete_category_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmation = false
                    viewModel.deleteCategory()
                    onBack()
                }) {
                    Text(stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarState)
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.category_details_label)) },
                navigationIcon = {
                    IconButton(onClick = {
                        when (val action = viewModel.onBackRequested()) {
                            CategoryDetailsViewModel.BackAction.DeleteEmpty -> {
                                coroutineScope.launch {
                                    snackbarState.showSnackbar(context.getString(R.string.removing_empty_category))
                                }
                                onBack()
                            }

                            is CategoryDetailsViewModel.BackAction.Warn -> {
                                coroutineScope.launch {
                                    snackbarState.showSnackbar(context.getString(action.messageRes))
                                }
                            }

                            CategoryDetailsViewModel.BackAction.Save -> onBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetToOriginal() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(id = R.string.refresh)
                        )
                    }
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete)
                        )
                    }
                },
                colors = mainTopAppBarColors()
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.semantics {
                    contentDescription = context.getString(R.string.add_item)
                },
                text = { Text(stringResource(id = R.string.add_item)) },
                icon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null
                    )
                },
                onClick = { viewModel.addThing() }
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_small)),
                value = uiState.currentCategoryName,
                onValueChange = { viewModel.onNameChange(it) },
                label = {
                    Text(
                        stringResource(R.string.hint_list_name),
                    )
                },
                colors = mainOutlinedTextfieldColors()
            )
            LabelText(text = stringResource(id = R.string.items))
            if (uiState.currentCategoryThings.isEmpty()) {
                Box(Modifier.weight(1f, fill = false)) {
                    EmptyStateScreen(
                        stringResource(id = R.string.no_items_tagline),
                        stringResource(id = R.string.no_items_message)
                    )
                }
            } else {
                LazyColumn(
                    Modifier
                        .weight(1f, fill = false)
                        .semantics { contentDescription = "items" },
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                ) {
                    itemsIndexed(uiState.currentCategoryThings) { index, item ->
                        key(item) {
                            EditableSingleLineItem(text = item,
                                update = { text ->
                                    viewModel.onThingChange(
                                        index,
                                        text
                                    )
                                },
                                remove = {
                                        text -> viewModel.removeThing(text)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

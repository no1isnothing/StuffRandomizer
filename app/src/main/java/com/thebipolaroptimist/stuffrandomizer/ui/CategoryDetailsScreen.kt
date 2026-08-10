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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.ui.components.EditableSingleLineItem
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

    LaunchedEffect(categoryId) {
        viewModel.load(categoryId)
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
                    IconButton(onClick = {
                        viewModel.deleteCategory()
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
            LazyColumn(Modifier.weight(1f, fill = false).semantics { contentDescription = "items" }) {
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
            Button(modifier = Modifier.wrapContentSize(),
                onClick = {
                    viewModel.addThing()
                }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add)
                )
            }
        }
    }
}

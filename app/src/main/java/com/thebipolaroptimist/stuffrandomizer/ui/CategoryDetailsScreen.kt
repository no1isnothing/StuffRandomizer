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
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.ui.components.EditableSingleLineItem
import com.thebipolaroptimist.stuffrandomizer.ui.components.LabelText
import kotlinx.coroutines.launch
import java.util.UUID


/**
 * A simple [Composable] to create/edit [Category]s.
 *
 * @param mainViewModel The ViewModel to use for interacting with data.
 * @param navigateBack A function to handle back navigation.
 * @param id The UUID for the [Category] to edit, if null create a new category.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailsScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    navigateBack: () -> Unit = {},
    id: String?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val category = id?.let {
        mainViewModel.categories
            .value?.first { c -> c.uid == UUID.fromString(id) }
    } ?: Category(UUID.randomUUID(), "", listOf())

    mainViewModel.setupCurrentCategory(category)

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.category_details_label)) },
                navigationIcon = {
                    IconButton(onClick = {
                        category.let {
                            if (mainViewModel.currentCategoryName.isEmpty() && mainViewModel.currentCategoryThings.isEmpty()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(context.getString(R.string.removing_empty_category))
                                }

                                mainViewModel.deleteCategory(category)
                                navigateBack()
                                return@IconButton
                            }

                            if (mainViewModel.currentCategoryThings.isEmpty()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(context.getString(R.string.warning_empty_item_list))
                                }
                                return@IconButton
                            }

                            if (mainViewModel.currentCategoryName.isEmpty()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(context.getString(R.string.warning_list_name_empty))
                                }
                                return@IconButton
                            }

                            it.things =
                                mainViewModel.currentCategoryThings.filter { s -> s.isNotEmpty() }
                                    .toList()
                            it.name = mainViewModel.currentCategoryName
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
                        mainViewModel.currentCategoryThings.clear()
                        category.let {
                            mainViewModel.currentCategoryName = it.name
                            mainViewModel.currentCategoryThings.addAll(it.things)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(id = R.string.refresh)
                        )
                    }
                    IconButton(onClick = {
                        mainViewModel.resetEditedParty()
                        mainViewModel.deleteCategory(category)
                        navigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete)
                        )
                    }
                }
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                value = mainViewModel.currentCategoryName,
                onValueChange = { mainViewModel.currentCategoryName = it },
                label = { Text(stringResource(R.string.hint_list_name)) })
            LabelText(text = stringResource(id = R.string.items))
            LazyColumn(Modifier.weight(1f, fill = false)) {
                itemsIndexed(mainViewModel.currentCategoryThings) { index, item ->
                    key(item) {
                        EditableSingleLineItem(text = item,
                            update = { text ->
                                mainViewModel.updateCurrentCategoryThings(
                                    index,
                                    text
                                )
                            },
                            remove = { text -> mainViewModel.currentCategoryThings.remove(text) }
                        )
                    }
                }
            }
            Button(modifier = Modifier.wrapContentSize(),
                onClick = {
                    mainViewModel.currentCategoryThings.add("")
                }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add)
                )
            }
        }
    }
}
package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.ui.components.ClickableTwoLineItem
import com.thebipolaroptimist.stuffrandomizer.ui.components.EmptyStateScreen
import com.thebipolaroptimist.stuffrandomizer.ui.components.mainTopAppBarColors

/**
 * A simple [Composable] for displaying [Category]s.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    viewModel: MainViewModel = hiltViewModel(),
    toCategoryCreation: () -> Unit = {},
    toCategoryEdit: (uuid: String) -> Unit = {},
) {
    val categoryList by viewModel.categories.observeAsState(listOf())
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.category_list_label)) },
                actions = {
                    IconButton(
                        onClick = { toCategoryCreation() },
                    ) {
                        Icon(Icons.Filled.Add, stringResource(R.string.add_match))
                    }
                },
                colors = mainTopAppBarColors())
        }) { padding ->
        if(categoryList.isEmpty()) {
            EmptyStateScreen(
                stringResource(R.string.no_categories_tagline),
                stringResource(R.string.no_categories_message)
            )
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .semantics { contentDescription = "items" }
            ) {
                items(categoryList) { item ->
                    ClickableTwoLineItem(
                        item.name,
                        item.things.joinToString()
                    ) { toCategoryEdit(item.uid.toString()) }
                }
            }
        }
    }
}

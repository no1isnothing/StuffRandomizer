package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category

/**
 * A simple [Composable] for displaying [Category]s.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    navigateBack: () -> Unit = {},
    toCategoryCreation: () -> Unit = {},
    toCategoryEdit: (uuid: String) -> Unit
) {
    val categoryList by mainViewModel.categories.observeAsState(listOf())
    Scaffold(
        topBar = {
            TopAppBar(title = { stringResource(id = R.string.category_list_fragment_label) },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { toCategoryCreation() },
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_match))
            }
        }) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(categoryList) { item ->
                CategoryItem(item, toCategoryEdit)
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    toCategoryEdit: (uuid: String) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                toCategoryEdit(category.uid.toString())
            }) {
        Column {
            Text(category.name)
            Text(text = category.things.joinToString())
        }

    }
}

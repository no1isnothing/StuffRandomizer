package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.fragment.findNavController
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

/**
 * A simple [Composable] for displaying [Category]s.
 */
@Composable
fun CategoryListScreen(mainViewModel: MainViewModel = hiltViewModel(),
                       toCategoryCreation: () -> Unit = {},
                       toCategoryEdit: (uuid: String) -> Unit) {
    val categoryList by mainViewModel.categories.observeAsState(listOf())
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { toCategoryCreation() },
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.add_match))
            }
        }) {
            padding ->
        LazyColumn(Modifier.fillMaxSize()
            .padding(padding)) {
            items(categoryList) { item ->
                CategoryItem(item, toCategoryEdit)
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category,
                 toCategoryEdit: (uuid: String) -> Unit) {
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

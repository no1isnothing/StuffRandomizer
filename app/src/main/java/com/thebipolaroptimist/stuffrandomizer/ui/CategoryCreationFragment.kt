package com.thebipolaroptimist.stuffrandomizer.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category

/**
 * A [Composable] to create [Category]s.
 */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CategoryCreationScreen(mainViewModel: MainViewModel = hiltViewModel(),
                               navigateBack: ()-> Unit = {},
                               toCategoryList: () -> Unit = {}) {
        val context = LocalContext.current
        Scaffold(
            topBar = { TopAppBar(title = { Text(stringResource(id = R.string.category_creation_fragment_label)) },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }) },
        ) { padding ->
            Column(Modifier.padding(padding)) {
                TextField(
                    value =  mainViewModel.newCategoryName,
                    onValueChange = {  mainViewModel.newCategoryName = it },
                    label = { Text(stringResource(R.string.hint_list_name)) },
                )
                TextField(
                    value = mainViewModel.newThing,
                    onValueChange = { mainViewModel.newThing = it },
                    label = { Text(stringResource(R.string.hint_item))}
                )
                Button(onClick = {
                    if(mainViewModel.newThing.isBlank()) {
                        Toast.makeText(context, context.getString(R.string.warning_empty_item), Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    if(mainViewModel.newThings.contains(mainViewModel.newThing)) {
                        Toast.makeText(context, context.getString(R.string.warning_duplicate_item), Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    mainViewModel.newThings.add(mainViewModel.newThing)
                    mainViewModel.newThing = ""
                }) {
                    Text(stringResource(R.string.add))
                }

                LazyColumn(
                    Modifier
                        .wrapContentSize()) {
                    items(mainViewModel.newThings) {
                            item -> Text(item)
                    }
                }
                Button(onClick = {
                    if (mainViewModel.newCategoryName.isEmpty()) {
                        Toast.makeText(context, context.getString(R.string.warning_list_name_empty), Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    if (mainViewModel.newThings.isEmpty()) {
                        Toast.makeText(context, context.getString(R.string.warning_empty_item_list), Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    mainViewModel.saveNewCategory()
                    toCategoryList()
                }) {
                    Text(stringResource(R.string.save))
                }
                Button(onClick = {
                    MaterialAlertDialogBuilder(context)
                        .setMessage(context.getString(R.string.discard_draft))
                        .setNegativeButton(context.getString(R.string.cancel)) { _, _ -> }
                        .setPositiveButton(context.getString(R.string.discard)) { _, _ ->
                            mainViewModel.resetNewCategory()
                            toCategoryList()
                        }
                        .show()
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }


package com.thebipolaroptimist.stuffrandomizer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

/**
 * A [Fragment] to create [Category]s.
 */
@AndroidEntryPoint
class CategoryCreationFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logger.atInfo().log("on create view")

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CategoryCreationScreen()
            }
        }
    }

    private fun launchDiscardDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.discard_draft))
            .setNegativeButton(resources.getString(R.string.cancel)) {_,_ ->}
            .setPositiveButton(resources.getString(R.string.discard)) { _,_ ->
                mainViewModel.resetCategoryState()
                findNavController().popBackStack()
            }
            .show()
    }

    @Composable
    fun CategoryCreationScreen() {
        Column {
            TextField(
                value =  mainViewModel.newCategoryName,
                onValueChange = {  mainViewModel.newCategoryName = it },
                label = { Text(resources.getString(R.string.hint_list_name)) },
            )
            TextField(
                value = mainViewModel.newThing,
                onValueChange = { mainViewModel.newThing = it },
                label = { Text(resources.getString(R.string.hint_item))}
            )
            Button(onClick = {
                if(mainViewModel.newThing.isBlank()) {
                    Toast.makeText(context, getString(R.string.warning_empty_item), Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }
                if(mainViewModel.newThings.contains(mainViewModel.newThing)) {
                    Toast.makeText(context, getString(R.string.warning_duplicate_item), Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }

                mainViewModel.newThings.add(mainViewModel.newThing)
                mainViewModel.newThing = ""
            }) {
                Text(resources.getString(R.string.add))
            }

            LazyColumn(Modifier.wrapContentSize()) {
                items(mainViewModel.newThings) {
                    item -> Text(item)
                }
            }
            Button(onClick = {
                if (mainViewModel.newCategoryName.isEmpty()) {
                    Toast.makeText(context, getString(R.string.warning_list_name_empty), Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }
                if (mainViewModel.newThings.isEmpty()) {
                    Toast.makeText(context, getString(R.string.warning_empty_item_list), Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }
                mainViewModel.saveCategory()
                findNavController().popBackStack()
            }) {
                Text(resources.getString(R.string.save))
            }
            Button(onClick = { launchDiscardDialog() }) {
                Text(resources.getString(R.string.cancel))
            }
        }

    }

    companion object {
        private val logger: FluentLogger = FluentLogger.forEnclosingClass()
    }
}
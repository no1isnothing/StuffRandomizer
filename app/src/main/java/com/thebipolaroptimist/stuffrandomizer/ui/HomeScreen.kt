package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.ui.components.mainTopAppBarColors
import com.thebipolaroptimist.stuffrandomizer.viewmodel.HomeViewModel

/**
 * A simple [Composable] as the default destination in the navigation.
 * Allows the user to launch [QuickSelectDialog].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var menuExpanded by remember {
        mutableStateOf(false)
    }
    var showDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.home_label)) },
                actions = {
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.menu)
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }) {
                        // TODO #7: Remove, hide behind flag, or update to be 'default data'
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.action_add_data)) },
                            onClick = { viewModel.addSampleData() })
                        // TODO #7: Remove, hide behind flag, or move to advanced settings with a warning
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.action_clear_data)) },
                            onClick = { viewModel.clearAllData() })
                    }
                },
                colors = mainTopAppBarColors()
            )
        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Button(onClick = { showDialog = true }) {
                Text(text = stringResource(R.string.quick_select))
            }
            if (showDialog) {
                QuickSelectDialog(
                    onDismiss = { showDialog = false },
                    categoryList = uiState.categories
                )
            }
        }
    }
}

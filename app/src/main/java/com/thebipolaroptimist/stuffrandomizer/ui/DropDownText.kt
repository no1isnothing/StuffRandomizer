package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.thebipolaroptimist.stuffrandomizer.R

@Composable
fun DropDownText(items: List<String>,
                 onSelect: (index: Int) -> Unit) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var selected by remember {
        mutableStateOf(0)
    }

    Box(Modifier.wrapContentSize()) {
        Row(Modifier.clickable { expanded = true }) {
            if (items.size > selected) {
                Text(items[selected])
            }
            Image(
                Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.expand)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(text = {
                    Text(text = item)
                },
                    onClick = {
                        expanded = false
                        selected = index
                        onSelect(index)
                    })
            }
        }

    }

}
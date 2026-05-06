package com.thebipolaroptimist.stuffrandomizer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.thebipolaroptimist.stuffrandomizer.R

/**
 * A [Composable] for displaying a drop down menu of text.
 *
 * @param items The items to display in the drop down
 * @param selectedIndex The index of the currently selected item
 * @param onSelect The function to call when an item is selected
 */
@Preview(showBackground = true)
@Composable
fun DropDownText(
    modifier: Modifier = Modifier,
    items: List<String> = listOf("Item", "Item 2", "Item3"),
    selectedIndex: Int = 0,
    onSelect: (index: Int) -> Unit = {}
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box(modifier.wrapContentSize().padding(dimensionResource(id = R.dimen.padding_small))) {
        Row(Modifier.clickable { expanded = true }) {
            if (items.size > selectedIndex) {
                Text(items[selectedIndex])
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
                        onSelect(index)
                    })
            }
        }

    }

}
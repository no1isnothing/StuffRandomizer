package com.thebipolaroptimist.stuffrandomizer.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.thebipolaroptimist.stuffrandomizer.R

/**
 *  A checkable [Composable] displaying one text item.
 *  @param name The text to display
 *  @param isChecked Whether the item is checked
 *  @param onCheck The function to call when the box is checked
 */
@Preview(showBackground = true)
@Composable
fun CheckableItem(
    modifier: Modifier = Modifier,
    name: String = "Category",
    isChecked: Boolean = false,
    onCheck: (state: Boolean) -> Unit = {}
) {
    Row(modifier = modifier.padding(dimensionResource(id = R.dimen.padding_small)),
        verticalAlignment = Alignment.CenterVertically
        ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { state ->
                onCheck(state)
            }
        )
        Text(
            text = name,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_text)))
    }
}
package com.thebipolaroptimist.stuffrandomizer.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.thebipolaroptimist.stuffrandomizer.R

/**
 *  A checkable [Composable] displaying one text item.
 *  @param name The text to display
 *  @param onCheck The function to call when the box is checked
 */
@Preview(showBackground = true)
@Composable
fun CheckableItem(name: String = "Category",
                  onCheck: (state: Boolean) -> Unit = {})  {
    var checked by rememberSaveable {
        mutableStateOf(false)
    }
    Row(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
        verticalAlignment = Alignment.CenterVertically
        ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { isChecked ->
                onCheck(isChecked)
                checked = isChecked
            }
        )
        Text(
            text = name,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_text)))
    }
}
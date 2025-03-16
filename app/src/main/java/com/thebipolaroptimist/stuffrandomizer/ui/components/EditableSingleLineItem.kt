package com.thebipolaroptimist.stuffrandomizer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.thebipolaroptimist.stuffrandomizer.R

/**
 * A [Composable] displaying a single editable line of text.
 *
 * @param text The text to display/update
 * @param position The first param to send to update function.
 *        (Likely the index in some external array)
 * @param update The function to call when text is changed
 * @param remove The function to call Clear icon is click
 */
@Preview(showBackground = true)
@Composable
fun EditableSingleLineItem(
    text: String = "Text",
    position: Int = 1,
    update: (position: Int, item: String) -> Unit = { _, _ -> },
    remove: (item: String) -> Unit = {}
) {
    var editedItem by rememberSaveable {
        mutableStateOf(text)
    }

    Row(
        Modifier
            .wrapContentSize()
            .padding(dimensionResource(id = R.dimen.padding_text))) {
        TextField(
            value = editedItem,
            onValueChange = {
                editedItem = it
                update(position, editedItem)
            },
            trailingIcon = {
                Icon(Icons.Default.Clear,
                    contentDescription = stringResource(R.string.clear),
                    modifier = Modifier
                        .clickable {
                            remove(editedItem)
                        }
                )
            }
        )
    }
}
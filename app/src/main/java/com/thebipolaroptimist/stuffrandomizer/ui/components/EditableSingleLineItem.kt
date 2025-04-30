package com.thebipolaroptimist.stuffrandomizer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.thebipolaroptimist.stuffrandomizer.R

/**
 * A [Composable] displaying a single editable line of text.
 *
 * @param text The text to display/update
 * @param update The function to call when text is changed
 * @param remove The function to call Clear icon is click
 */
@Preview(showBackground = true)
@Composable
fun EditableSingleLineItem(
    text: String = "Text",
    update: (item: String) -> Unit = {},
    remove: (item: String) -> Unit = {}
) {

    var editedItem by remember {
        mutableStateOf(TextFieldValue(text))
    }

    Card(
        Modifier
            .wrapContentSize()
            .padding(dimensionResource(id = R.dimen.padding_text)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer),) {
        TextField(
            modifier = Modifier.semantics { contentDescription = "editable_item" },
            value = editedItem,
            onValueChange = {
                editedItem = it
                update(it.text)
            },
            trailingIcon = {
                Icon(Icons.Default.Clear,
                    contentDescription = stringResource(R.string.clear),
                    modifier = Modifier
                        .clickable {
                            remove(editedItem.toString())
                        }
                )
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
    }
}
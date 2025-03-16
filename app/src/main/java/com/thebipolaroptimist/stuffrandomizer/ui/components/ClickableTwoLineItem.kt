package com.thebipolaroptimist.stuffrandomizer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.thebipolaroptimist.stuffrandomizer.R

/**
 * A clickable [Composable] displaying two lines of text.
 *
 * @param label The top and large text
 * @param body The bottom and smaller text
 * @param data A string to be passed to [onClick]
 * @param onClick The function to call when this is clicked
 */
@Preview(showBackground = true)
@Composable
fun ClickableTwoLineItem(
    label: String = "Label",
    body: String = "Body",
    data: String = "data",
    onClick: (data: String) -> Unit = {}
) {
    Row(
        Modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .fillMaxWidth()
            .clickable {
                onClick(data)
            }) {
        Column(Modifier.padding(dimensionResource(R.dimen.padding_text))) {
            Text(
                label, style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                body, style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
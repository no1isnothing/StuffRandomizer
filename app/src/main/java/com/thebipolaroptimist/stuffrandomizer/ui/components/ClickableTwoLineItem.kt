package com.thebipolaroptimist.stuffrandomizer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
 * @param onClick The function to call when this is clicked
 */
@Preview(showBackground = true)
@Composable
fun ClickableTwoLineItem(
    label: String = "Label",
    body: String = "Body",
    onClick: () -> Unit = {}
) {
    Card(
        Modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
    ) {
        Column(Modifier.padding(dimensionResource(R.dimen.padding_text)).align(Alignment.CenterHorizontally)) {
            Text(
                label, style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                body, style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
package com.thebipolaroptimist.stuffrandomizer.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.thebipolaroptimist.stuffrandomizer.R

/**
 * A [Composable] for displaying small label text.
 *
 * @param text The text to display
 */
@Preview(showBackground = true)
@Composable
fun LabelText(text: String = "Text") {
    Text(
        text = text,
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
        style = MaterialTheme.typography.titleMedium
    )
}
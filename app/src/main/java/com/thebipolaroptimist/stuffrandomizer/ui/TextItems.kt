package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.thebipolaroptimist.stuffrandomizer.R

@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
        style = MaterialTheme.typography.titleMedium
    )
}
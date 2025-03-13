package com.thebipolaroptimist.stuffrandomizer.ui

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
import com.thebipolaroptimist.stuffrandomizer.R

@Composable
fun CheckableItem(categoryName: String,
                  onCheck: (state: Boolean) -> Unit)  {
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
            text = categoryName,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_text)))
    }
}
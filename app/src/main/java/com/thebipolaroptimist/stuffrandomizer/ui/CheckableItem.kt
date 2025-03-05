package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun CheckableItem(categoryName: String,
                  onCheck: (state: Boolean) -> Unit)  {
    var checked by rememberSaveable {
        mutableStateOf(false)
    }
    Row() {
        Checkbox(
            checked = checked,
            onCheckedChange = { isChecked ->
                onCheck(isChecked)
                checked = isChecked
            }
        )
        Text(categoryName)
    }
}
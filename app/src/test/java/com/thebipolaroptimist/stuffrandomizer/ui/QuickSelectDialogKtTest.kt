package com.thebipolaroptimist.stuffrandomizer.ui

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thebipolaroptimist.stuffrandomizer.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickSelectDialogKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun init_showsDefaultOptions() {
        composeTestRule.setContent {
            QuickSelectDialog()
        }

        composeTestRule.onNodeWithText(context.getString(R.string.select_existing_list))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.enter_new_list))
            .assertIsDisplayed()
    }

    @Test
    fun enterNewListThenRoll_showsSelectedItem() {
        composeTestRule.setContent {
            QuickSelectDialog()
        }

        composeTestRule.onNodeWithText(context.getString(R.string.enter_new_list)).performClick()
        composeTestRule.onNodeWithTag("editable_item").performTextInput("Only Item")
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.roll)).performClick()

        composeTestRule.onNodeWithText("Only Item").assertIsDisplayed()
    }

    @Test
    fun newDialogState_startsBackAtDefault() {
        // A fresh QuickSelectDialogState (as created each time the dialog is re-shown, since
        // it's only remembered while the dialog is in composition) always starts at DEFAULT,
        // regardless of what a previous instance's state was.
        val state = QuickSelectDialogState()
        state.startNewList()

        composeTestRule.setContent {
            QuickSelectDialog(state = QuickSelectDialogState())
        }

        composeTestRule.onNodeWithText(context.getString(R.string.select_existing_list))
            .assertIsDisplayed()
    }
}

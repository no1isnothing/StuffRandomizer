package com.thebipolaroptimist.stuffrandomizer.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thebipolaroptimist.stuffrandomizer.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.fakes.FakeRepository
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    // Helper function to set content and improve readability
    private fun setupScreenWith(viewModel: MainViewModel) {
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel)
        }
    }

    @Test
    fun init_uiCorrectDialogHidden() {
        // Setup
        setupScreenWith(MainViewModel(FakeRepository()))

        // Verify UI Fields
        composeTestRule.onNodeWithText(context.getString(R.string.quick_select)).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.quick_select_dialog))
            .assertIsNotDisplayed()
    }

    @Test
    fun onQuickSelectClicked_quickSelectDialogDisplay() {
        // Setup
        setupScreenWith(MainViewModel(FakeRepository()))

        // Click Quick Select Button
        composeTestRule.onNodeWithText(context.getString(R.string.quick_select)).performClick()

        //Verify Dialog opens
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.quick_select_dialog))
            .assertIsDisplayed()

    }
}
package com.thebipolaroptimist.stuffrandomizer.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.fakes.FakeRepository
import com.thebipolaroptimist.stuffrandomizer.fakes.UUID_ONE
import com.thebipolaroptimist.stuffrandomizer.fakes.UUID_ZERO
import com.thebipolaroptimist.stuffrandomizer.viewmodel.PartyCreationViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PartyCreationScreenKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private fun setupScreenWith(
        viewModel: PartyCreationViewModel,
        toPartyEdit: (uuid: String) -> Unit = {},
    ) {
        composeTestRule.setContent {
            PartyCreationScreen(viewModel = viewModel, toPartyEdit = toPartyEdit)
        }
    }

    @Test
    fun initialScreen_nameFieldEmpty() {
        setupScreenWith(PartyCreationViewModel(FakeRepository()))

        composeTestRule.onNodeWithText(context.getString(R.string.hint_match_name))
            .assertIsDisplayed()
    }

    @Test
    fun rollWithEmptyName_showsNameWarning() {
        setupScreenWith(PartyCreationViewModel(FakeRepository()))

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.roll)).performClick()

        composeTestRule.onNodeWithText(context.getString(R.string.warning_match_name_empty))
            .assertIsDisplayed()
    }

    @Test
    fun rollWithNameButNoSelection_showsAssignmentWarning() {
        val category = Category(UUID_ZERO, "Category 0", listOf("Item 1"))
        val repository = FakeRepository().apply { categoryMap[category.uid] = category }

        setupScreenWith(PartyCreationViewModel(repository))

        composeTestRule.onNodeWithText(context.getString(R.string.hint_match_name))
            .performTextInput("My Party")
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.roll)).performClick()

        composeTestRule.onNodeWithText(context.getString(R.string.warning_match_assignments_empty))
            .assertIsDisplayed()
    }

    @Test
    fun rollWithNameAndSelection_navigatesToPartyEdit() {
        val category = Category(UUID_ZERO, "Category 0", listOf("Item 1"))
        val category1 = Category(UUID_ONE, "Category 1", listOf("Item 2"))
        val repository = FakeRepository().apply {
            categoryMap[category.uid] = category
            categoryMap[category1.uid] = category1
        }
        var editedUuid: String? = null

        setupScreenWith(PartyCreationViewModel(repository), toPartyEdit = { editedUuid = it })

        composeTestRule.onNodeWithText(context.getString(R.string.hint_match_name))
            .performTextInput("My Party")
        composeTestRule.onAllNodes(isToggleable())[0].performClick()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.roll)).performClick()

        assert(editedUuid != null)
    }
}

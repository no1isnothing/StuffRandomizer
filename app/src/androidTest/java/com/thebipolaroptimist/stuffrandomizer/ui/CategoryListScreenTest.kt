package com.thebipolaroptimist.stuffrandomizer.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thebipolaroptimist.stuffrandomizer.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.fakes.FakeRepository
import com.thebipolaroptimist.stuffrandomizer.fakes.UUID_ONE
import com.thebipolaroptimist.stuffrandomizer.fakes.UUID_TWO
import com.thebipolaroptimist.stuffrandomizer.fakes.UUID_ZERO
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


// TODO: Change to robelectric for faster tests
@RunWith(AndroidJUnit4::class)
class CategoryListScreenKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val context = ApplicationProvider.getApplicationContext<Context>()

    // Helper function to set content and improve readability
    private fun setupScreenWith(viewModel: MainViewModel) {
        composeTestRule.setContent {
            CategoryListScreen(viewModel = viewModel)
        }
    }

    @Test
    fun init_withEmptyList_emptyStateScreenDisplayed() {
        // Setup with no categories
        setupScreenWith(MainViewModel(FakeRepository()))

        // Verify UI display empty message and tagline
        composeTestRule.onNodeWithText(context.getString(R.string.no_categories_message)).assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.no_categories_tagline)).assertExists()
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.add_match))
    }

    @Test
    fun init_withCategories_listDisplayed() {
        // Setup with categories
        val category = Category(UUID_ZERO, "Category 0", listOf("Item 1", "Item 2"))
        val category1 = Category(UUID_ONE, "Category 1", listOf("Item 1-1", "Item 1-2"))
        val category2 = Category(UUID_TWO, "Category 2", listOf("Item 2-1", "Item 2-2"))
        val repository = FakeRepository().apply {
            categoryMap[category.uid] = category
            categoryMap[category1.uid] = category1
            categoryMap[category2.uid] = category2
        }
        val viewModel = MainViewModel(repository)
        setupScreenWith(viewModel)

        // Verify UI Displays List of Categories
        composeTestRule.onAllNodes(hasParent(hasContentDescription("items"))).assertCountEquals(3)
        composeTestRule.onNodeWithText("Category 0").assertIsDisplayed()
        composeTestRule.onNodeWithText("Category 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Category 2").assertIsDisplayed()
    }
}
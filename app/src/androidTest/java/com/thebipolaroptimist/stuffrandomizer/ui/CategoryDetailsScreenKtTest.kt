package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thebipolaroptimist.stuffrandomizer.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.data.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

class FakeRepository : Repository {
    val categoryMap = mutableMapOf<UUID, Category>()
    val partyMap = mutableMapOf<UUID, Party>()
    override fun getAllParties(): Flow<List<Party>> {
        return flowOf(partyMap.entries.map { it.value })
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return flowOf(categoryMap.entries.map { it.value })
    }

    override suspend fun getCategoriesByName(names: List<String>): List<Category> {
        return categoryMap.values.filter { names.contains(it.name) }
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return categoryMap.values.firstOrNull { it.name == name }
    }

    override suspend fun insertParty(party: Party) {
        partyMap[party.uid] = party
    }

    override suspend fun insertCategory(category: Category) {
        categoryMap[category.uid] = category
    }

    override suspend fun deleteCategory(category: Category) {
        categoryMap.remove(category.uid)
    }

    override suspend fun insertCategories(categories: List<Category>) {
        categoryMap.putAll(categories.map { it.uid to it })
    }

    override suspend fun deleteAllParties() {
        partyMap.clear()
    }

    override suspend fun deleteAllCategories() {
        categoryMap.clear()
    }
}

// unused by might need later
//composeTestRule.onNodeWithTag("items").onChildAt(0).performTextInput("item1")
// add espresso testing?
// change out to inject a different module or test runner instead
// fill in rest of tests



@RunWith(AndroidJUnit4::class)
class CategoryDetailsScreenKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun int_withNoCategory_uiFieldsEmpty() {
        val fakeRepository = FakeRepository()
        // Setup and check the initial state of the screen
        composeTestRule.setContent {
            CategoryDetailsScreen(viewModel = MainViewModel(fakeRepository),
                categoryId = null)
        }
        composeTestRule.onNodeWithText("List Name").assert(hasText(""))
        composeTestRule.onNodeWithContentDescription("Add").assertExists()
        composeTestRule.onAllNodes(hasParent(hasContentDescription("items"))).assertCountEquals(0)
    }

    val UUID_ZERO_STRING = "00000000-0000-0000-0000-000000000000"
    val UUID_ZERO = UUID.fromString(UUID_ZERO_STRING)

    @Test
    fun init_withCategory_uiFieldsPopulated() {
        // Setup and check the initial state of the screen
        val category = Category(
            UUID_ZERO,
            "Category 1",
            listOf("Item 1", "Item 2"))

        val repository = FakeRepository()
        repository.categoryMap[category.uid] = category
        val viewModel = MainViewModel(repository)
        viewModel.categories.observeForever {  }
        composeTestRule.setContent {
            CategoryDetailsScreen(viewModel = viewModel,
                categoryId = UUID_ZERO_STRING)
        }

        composeTestRule.onNodeWithText("List Name").assert(hasText("Category 1"))
        composeTestRule.onAllNodes(hasParent(hasContentDescription("items"))).assertCountEquals(2)
        composeTestRule.onNodeWithText("Item 2").assertExists()
        composeTestRule.onNodeWithText("Item 1").assertExists()
    }

    @Test
    fun backNavigation_withCategory_categorySaved() {
        // Check that clicking the back button saves the current category 
        // (name and items) if they have been changed.
        val fakeRepository = FakeRepository()
        composeTestRule.setContent {
            CategoryDetailsScreen(viewModel = MainViewModel(fakeRepository),
                categoryId = null)
        }

        // Perform actions
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        composeTestRule.onNodeWithText("List Name").performTextInput("List 1")
        composeTestRule.onNodeWithContentDescription("editable_item").performTextInput("item1")
        composeTestRule.onNodeWithContentDescription("back").performClick()

        // Verify the updated UI state
        composeTestRule.onNodeWithContentDescription("editable_item").assert(hasText("item1"))
        composeTestRule.onNodeWithText("List Name").assert(hasText("List 1"))
        composeTestRule.onAllNodes(hasParent(hasContentDescription("items"))).assertCountEquals(1)

        // Verify that the category was saved
        assert(fakeRepository.categoryMap.size == 1)
        // check equality in one step?
        val category = fakeRepository.categoryMap.values.first()
        assert(category.name == "List 1")
        assert(category.things.size == 1)
        assert(category.things[0] == "item1")
    }

    @Test
    fun backNavigation_EmptyCategory_categoryNotSaved() {
        // Ensure that if the category name and all items are empty, 
        // clicking back deletes the category and navigates back.
        val fakeRepository = FakeRepository()
        composeTestRule.setContent {
            CategoryDetailsScreen(viewModel = MainViewModel(fakeRepository),
                categoryId = null)
        }

        // Perform actions
        composeTestRule.onNodeWithContentDescription("back").performClick()

        assert(fakeRepository.categoryMap.isEmpty())
        composeTestRule.onNodeWithText("Removing Empty Category").assertExists()
    }

    @Test
    fun `Back Navigation   Empty Items`() {
        // Verify that if the category has a name but no items, 
        // a snackbar warning is displayed and the category is not saved.
        val fakeRepository = FakeRepository()
        composeTestRule.setContent {
            CategoryDetailsScreen(viewModel = MainViewModel(fakeRepository),
                categoryId = null)
        }

        // Perform actions
        composeTestRule.onNodeWithText("List Name").performTextInput("List 1")
        composeTestRule.onNodeWithContentDescription("back").performClick()

        assert(fakeRepository.categoryMap.isEmpty())
        composeTestRule.onNodeWithText("Can\'t Save Category Without Items").assertExists()
    }

    @Test
    fun `Back Navigation   Empty Name`() {
        // Check that if the category has items but no name, a snackbar 
        // warning is shown and the category is not saved.
        val fakeRepository = FakeRepository()
        composeTestRule.setContent {
            CategoryDetailsScreen(viewModel = MainViewModel(fakeRepository),
                categoryId = null)
        }

        // Perform actions
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        composeTestRule.onNodeWithContentDescription("editable_item").performTextInput("item1")
        composeTestRule.onNodeWithContentDescription("back").performClick()

        assert(fakeRepository.categoryMap.isEmpty())
        composeTestRule.onNodeWithText("Can\'t Save Category Without Name").assertExists()
    }

    @Test
    fun `Refresh Button Functionality`() {
        // Test that the refresh button resets the category name and items 
        // to their original values when the screen was loaded.
        // Setup
        val category = Category(
            UUID_ZERO,
            "Category 1",
            listOf("Item 1", "Item 2"))

        val repository = FakeRepository()
        repository.categoryMap[category.uid] = category
        val viewModel = MainViewModel(repository)
        viewModel.categories.observeForever {  }
        composeTestRule.setContent {
            CategoryDetailsScreen(viewModel = viewModel,
                categoryId = UUID_ZERO_STRING)
        }

        // Edit and refresh
        composeTestRule.onNodeWithText("List Name").performTextInput("List 1")
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        composeTestRule.onAllNodesWithContentDescription("editable_item")[0].performTextInput("item1")
        composeTestRule.onAllNodesWithContentDescription("editable_item")[3].performTextInput("item one million")
        composeTestRule.onNodeWithContentDescription("refresh").performClick()

        // Verify Original Values
        composeTestRule.onNodeWithText("List Name").assert(hasText("Category 1"))
        composeTestRule.onAllNodes(hasParent(hasContentDescription("items"))).assertCountEquals(2)
        composeTestRule.onNodeWithText("Item 2").assertExists()
        composeTestRule.onNodeWithText("Item 1").assertExists()
    }

    @Test
    fun `Delete Button Functionality`() {
        // Verify that the delete button removes the current category and 
        // navigates back.
        // TODO implement test
    }

    @Test
    fun `Category Name Change`() {
        // Ensure that changing the text in the category name OutlinedTextField 
        // updates the viewModel's currentCategoryName.
        // TODO implement test
    }

    @Test
    fun `Adding Items`() {
        // Test that clicking the add button adds a new empty item to the list.
        // TODO implement test
    }

    @Test
    fun `Editing Item Content`() {
        // Verify that changing the text in an item's EditableSingleLineItem 
        // updates the corresponding item in currentCategoryThings.
        // TODO implement test
    }

    @Test
    fun `Removing Items`() {
        // Check that clicking the remove button on an EditableSingleLineItem 
        // removes the item from currentCategoryThings.
        // TODO implement test
    }

    @Test
    fun `Empty Item Strings Are Removed`() {
        //Check that if the category is saved, only non empty item strings are saved
        // TODO implement test
    }


    @Test
    fun `Non Existent Category ID`() {
        // Test behavior when a non-existent or invalid categoryId is provided.
        // TODO implement test
    }

    @Test
    fun `Large Category Name`() {
        // Test behavior with a very long category name to ensure 
        // UI components handle it correctly without breaking
        // TODO implement test
    }

    @Test
    fun `Large Number of Items`() {
        // Check how the UI handles a category with a large number 
        // of items (e.g., more than 100).
        // TODO implement test
    }

    @Test
    fun `Empty String in Category items`() {
        // verify that when user adds empty strings, and clicks the back button
        // that these empty strings are NOT saved to the category.
        // TODO implement test
    }

    @Test
    fun `Invalid Category ID Format`() {
        // Verify that an invalid Category ID format (not a UUID) 
        // is handled gracefully without causing crashes
        // TODO implement test
    }

}
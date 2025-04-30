package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thebipolaroptimist.stuffrandomizer.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.data.Repository
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


class FakeRepository : Repository {
    var lastCategory: Category? = null
    override fun getAllParties(): Flow<List<Party>> {
        return flowOf(emptyList())
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return flowOf(emptyList())
    }

    override suspend fun getCategoriesByName(names: List<String>): List<Category> {
        TODO("Not yet implemented")
    }

    override suspend fun getCategoryByName(name: String): Category? {
        TODO("Not yet implemented")
    }

    override suspend fun insertParty(party: Party) {
        TODO("Not yet implemented")
    }

    override suspend fun insertCategory(category: Category) {
        lastCategory = category
    }

    override suspend fun deleteCategory(category: Category) {
        TODO("Not yet implemented")
    }

    override suspend fun insertCategories(categories: List<Category>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllParties() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllCategories() {
        TODO("Not yet implemented")
    }
}

// unused by might need later
//composeTestRule.onNodeWithTag("items").onChildAt(0).performTextInput("item1")

@RunWith(AndroidJUnit4::class)
class CategoryDetailsScreenKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `New Category Creation`() {
        val fakeRepository = FakeRepository()
        // Setup and check the initial state of the screen
        composeTestRule.setContent {
            CategoryDetailsScreen(viewModel = MainViewModel(fakeRepository),
                categoryId = null)
        }
        composeTestRule.onNodeWithText("List Name").assert(hasText(""))
        composeTestRule.onNodeWithContentDescription("Add").assertExists()
        composeTestRule.onAllNodes(hasParent(hasTestTag("items"))).assertCountEquals(0)

        // Perform actions
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        composeTestRule.onNodeWithText("List Name").performTextInput("List 1")
        composeTestRule.onNodeWithContentDescription("editable_item").performTextInput("item1")
        composeTestRule.onNodeWithContentDescription("back").performClick()

        // Verify the updated state
        composeTestRule.onNodeWithContentDescription("editable_item").assert(hasText("item1"))
        composeTestRule.onNodeWithText("List Name").assert(hasText("List 1"))
        composeTestRule.onAllNodes(hasParent(hasTestTag("items"))).assertCountEquals(1)

        // Verify that the category was saved
        assert(fakeRepository.lastCategory != null)
        assertNotNull(fakeRepository.lastCategory?.name)
        assert(fakeRepository.lastCategory?.name  == "List 1")
        assertNotNull(fakeRepository.lastCategory?.things)
        assert(fakeRepository.lastCategory?.things?.size == 1)
        assert(fakeRepository.lastCategory?.things?.get(0) == "item1")
    }

    @Test
    fun `Category Editing`() {
        // Test editing an existing category by providing a valid categoryId.
        // TODO implement test
    }

    @Test
    fun `Back Navigation   Save`() {
        // Check that clicking the back button saves the current category 
        // (name and items) if they have been changed.
        // TODO implement test
    }

    @Test
    fun `Back Navigation   Empty Name   Items`() {
        // Ensure that if the category name and all items are empty, 
        // clicking back deletes the category and navigates back.
        // TODO implement test
    }

    @Test
    fun `Back Navigation   Empty Items`() {
        // Verify that if the category has a name but no items, 
        // a snackbar warning is displayed and the category is not saved.
        // TODO implement test
    }

    @Test
    fun `Back Navigation   Empty Name`() {
        // Check that if the category has items but no name, a snackbar 
        // warning is shown and the category is not saved.
        // TODO implement test
    }

    @Test
    fun `Refresh Button Functionality`() {
        // Test that the refresh button resets the category name and items 
        // to their original values when the screen was loaded.
        // TODO implement test
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
    fun `Snackbar   Empty Category`() {
        //Verify a snackbar is shown when name and list is empty, if user clicks back
        // TODO implement test
    }

    @Test
    fun `Snackbar   Empty List`() {
        //Verify a snackbar is shown when list is empty, if user clicks back
        // TODO implement test
    }

    @Test
    fun `Snackbar   Empty Category Name`() {
        //Verify a snackbar is shown when the category name is empty, if user clicks back
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
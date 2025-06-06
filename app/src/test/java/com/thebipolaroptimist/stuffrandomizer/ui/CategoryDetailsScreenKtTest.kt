package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thebipolaroptimist.stuffrandomizer.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.fakes.FakeRepository
import com.thebipolaroptimist.stuffrandomizer.fakes.UUID_ZERO
import com.thebipolaroptimist.stuffrandomizer.fakes.UUID_ZERO_STRING
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryDetailsScreenKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Helper function to set content and improve readability
    private fun setupScreenWith(viewModel: MainViewModel, categoryId: String?) {
        composeTestRule.setContent {
            CategoryDetailsScreen(viewModel = viewModel, categoryId = categoryId)
        }
    }

    @Test
    fun initialScreen_withNoCategory_uiFieldsEmpty() {
        // Setup with no initial category
        setupScreenWith(MainViewModel(FakeRepository()), null)

        // Verify UI fields are empty
        composeTestRule.onNodeWithText("List Name").assert(hasText(""))
        composeTestRule.onNodeWithContentDescription("Add").assertExists()
        composeTestRule.onAllNodes(hasParent(hasContentDescription("items"))).assertCountEquals(0)
    }

    @Test
    fun init_withCategory_uiFieldsPopulated() {
        // Setup with a pre-populated category
        val category = Category(UUID_ZERO, "Category 1", listOf("Item 1", "Item 2"))
        val repository = FakeRepository().apply { categoryMap[category.uid] = category }
        val viewModel = MainViewModel(repository)
        viewModel.categories.observeForever { }
        setupScreenWith(viewModel, UUID_ZERO_STRING)

        // Verify that UI fields are populated
        composeTestRule.onNodeWithText("List Name").assert(hasText("Category 1"))
        composeTestRule.onAllNodes(hasParent(hasContentDescription("items"))).assertCountEquals(2)
        composeTestRule.onNodeWithText("Item 2").assertExists()
        composeTestRule.onNodeWithText("Item 1").assertExists()
    }

    @Test
    fun backNavigation_withNewCategory_categorySaved() {
        // Setup with no initial category
        val fakeRepository = FakeRepository()
        setupScreenWith(MainViewModel(fakeRepository), null)

        // Perform actions to create and save a new category
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        composeTestRule.onNodeWithText("List Name").performTextInput("List 1")
        composeTestRule.onNodeWithContentDescription("editable_item").performTextInput("item1")
        composeTestRule.onNodeWithContentDescription("back").performClick()


        // Verify that the category was saved correctly
        val savedCategory = fakeRepository.categoryMap.values.first()
        assertEquals(1, fakeRepository.categoryMap.size)
        assertEquals("List 1", savedCategory.name)
        assertEquals(listOf("item1"), savedCategory.things)

        // Verify the updated UI state
        composeTestRule.onNodeWithContentDescription("editable_item").assert(hasText("item1"))
        composeTestRule.onNodeWithText("List Name").assert(hasText("List 1"))
        composeTestRule.onAllNodes(hasParent(hasContentDescription("items"))).assertCountEquals(1)
    }

    @Test
    fun backNavigation_withEmptyNewCategory_categoryNotSaved() {
        // Setup with no initial category
        val fakeRepository = FakeRepository()
        setupScreenWith(MainViewModel(fakeRepository), null)

        // Perform actions (clicking back without adding name or item)
        composeTestRule.onNodeWithContentDescription("back").performClick()


        // Verify that the category was not saved and warning is displayed
        assertTrue(
            "Category should not be saved in the repository when there are no name or items.",
            fakeRepository.categoryMap.isEmpty()
        )
        composeTestRule.onNodeWithText("Removing Empty Category").assertIsDisplayed()
    }

    @Test
    fun backNavigation_withNameButNoItems_showsWarningAndDoesNotSave() {
        // Setup with no initial category
        val fakeRepository = FakeRepository()
        setupScreenWith(MainViewModel(fakeRepository), null)

        // Perform actions (clicking back after adding name but no items)
        composeTestRule.onNodeWithText("List Name").performTextInput("List 1")
        composeTestRule.onNodeWithContentDescription("back").performClick()

        // Verify category not saved and warning displayed
        assertTrue(
            "Category should not be saved in the repository when there are no items.",
            fakeRepository.categoryMap.isEmpty()
        )
        composeTestRule.onNodeWithText("Can\'t Save Category Without Items").assertIsDisplayed()
    }

    @Test
    fun backNavigation_withItemsButNoName_showsWarningAndDoesNotSave() {
        // Setup with no initial category
        val fakeRepository = FakeRepository()
        setupScreenWith(MainViewModel(fakeRepository), null)

        // Perform actions (clicking back after adding items but no name)
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        composeTestRule.onNodeWithContentDescription("editable_item").performTextInput("item1")
        composeTestRule.onNodeWithContentDescription("back").performClick()


        // Verify category not saved and warning displayed
        assertTrue(
            "Category should not be saved in the repository when there are no list name.",
            fakeRepository.categoryMap.isEmpty()
        )
        composeTestRule.onNodeWithText("Can\'t Save Category Without Name").assertIsDisplayed()
    }

    @Test
    fun refreshButton_resetsCategoryToOriginalState() {
        // Setup with preexisting category
        val category = Category(
            UUID_ZERO,
            "Category 1",
            listOf("Item 1", "Item 2")
        )

        val repository = FakeRepository()
        repository.categoryMap[category.uid] = category
        val viewModel = MainViewModel(repository)
        viewModel.categories.observeForever { }
        setupScreenWith(viewModel, UUID_ZERO_STRING)


        // Edit the category and then refresh
        composeTestRule.onNodeWithText("List Name").performTextInput("List 1")
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        composeTestRule.onAllNodesWithContentDescription("editable_item")[0].performTextInput("item1")
        composeTestRule.onAllNodesWithContentDescription("editable_item")[2].performTextInput("item one million")
        composeTestRule.onNodeWithContentDescription("refresh").performClick()

        // Verify Original Values
        composeTestRule.onNodeWithText("List Name").assert(hasText("Category 1"))
        composeTestRule.onAllNodes(hasParent(hasContentDescription("items"))).assertCountEquals(2)
        composeTestRule.onNodeWithText("Item 2").assertExists()
        composeTestRule.onNodeWithText("Item 1").assertExists()
    }

    @Test
    fun deleteButton_removesCategoryFromRepository() {
        // Setup with a pre-populated category
        val category = Category(UUID_ZERO, "Category 1", listOf("Item 1"))
        val repository = FakeRepository().apply { categoryMap[category.uid] = category }
        val viewModel = MainViewModel(repository)
        viewModel.categories.observeForever { }
        setupScreenWith(viewModel, UUID_ZERO_STRING)

        // Perform delete action
        composeTestRule.onNodeWithContentDescription("Delete").performClick()

        // Verify that the category is removed from the repository
        assertTrue(
            "Category should be removed from the repository after deleting.",
            repository.categoryMap.isEmpty()
        )
    }

    @Test
    fun categoryNameChange_updatesViewModel() {
        // Setup with a pre-populated category
        val category = Category(UUID_ZERO, "Category 1", listOf("Item 1"))
        val repository = FakeRepository().apply { categoryMap[category.uid] = category }
        val viewModel = MainViewModel(repository)
        viewModel.categories.observeForever { }
        setupScreenWith(viewModel, UUID_ZERO_STRING)

        // Perform name change
        val newName = "New Category Name"
        composeTestRule.onNodeWithText("List Name").performTextClearance()
        composeTestRule.onNodeWithText("List Name").performTextInput(newName)

        // Verify that the name is updated in the view model
        composeTestRule.onNodeWithText("List Name").assert(hasText(newName))
        assertEquals(newName, viewModel.currentCategoryName)
    }


    @Test
    fun clickingAddButton_addsItemsToList() {
        // Setup with no existing category
        val fakeRepository = FakeRepository()
        setupScreenWith(MainViewModel(fakeRepository), null)

        // Click add button twice
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        composeTestRule.onNodeWithContentDescription("Add").performClick()

        // Verify 2 empty items are added
        composeTestRule.onAllNodes(hasParent(hasContentDescription("items"))).assertCountEquals(2)
    }


    @Test
    fun editingItem_updatesViewModelAndField() {
        // Setup with pre-populated category
        val category = Category(UUID_ZERO, "Category 1", listOf("Item 1"))
        val repository = FakeRepository().apply { categoryMap[category.uid] = category }
        val viewModel = MainViewModel(repository)
        setupScreenWith(viewModel, null)

        // Add an item and change its content
        composeTestRule.onNodeWithContentDescription("Add").performClick()

        composeTestRule.onAllNodesWithContentDescription("editable_item")[0].performTextClearance()
        composeTestRule.onAllNodesWithContentDescription("editable_item")[0].performTextInput("New Item Content")

        // Verify that the item content is updated in the view model's currentCategoryThings
        assertEquals(
            listOf("New Item Content"),
            viewModel.currentCategoryThings
        )
        composeTestRule.onAllNodesWithContentDescription("editable_item")[0].assert(hasText("New Item Content"))
    }


    @Test
    fun clickingClearButton_removesItemFromList() {
        // Setup with a category with 3 items
        val category = Category(UUID_ZERO, "Category 1", listOf("Item 1", "Item 2", "Item 3"))
        val repository = FakeRepository().apply { categoryMap[category.uid] = category }
        val viewModel = MainViewModel(repository)
        viewModel.categories.observeForever { }
        setupScreenWith(viewModel, UUID_ZERO_STRING)

        // Click Remove Button
        composeTestRule.onAllNodesWithContentDescription("Clear")[1].performClick()

        // Verify Item has been removed
        composeTestRule.onAllNodes(hasParent(hasContentDescription("items"))).assertCountEquals(2)
        assertEquals(listOf("Item 1", "Item 3"), viewModel.currentCategoryThings)
    }

    @Test
    fun savingListWithEmptyStrings_emptyStringsAreRemoved() {
        // Check that if the category is saved, only non empty item strings are saved
        val category = Category(UUID_ZERO, "Category 1", listOf("Item 1", "Item 3"))
        val repository = FakeRepository().apply { categoryMap[category.uid] = category }
        val viewModel = MainViewModel(repository)
        viewModel.categories.observeForever { }
        setupScreenWith(viewModel, UUID_ZERO_STRING)

        // Add two empty items using the add button
        composeTestRule.onNodeWithContentDescription("Add").performClick()
        composeTestRule.onNodeWithContentDescription("Add").performClick()

        // Simulate saving by clicking back button
        composeTestRule.onNodeWithContentDescription("back").performClick()

        // Verify empty strings have been removed
        val savedCategory = repository.categoryMap.values.first()
        assertEquals(listOf("Item 1", "Item 3"), savedCategory.things)
    }

    @Test
    fun nonExistentCategoryId_showsEmptyScreen() {
        // Setup screen with non-existent category ID
        val fakeRepository = FakeRepository()
        val viewModel = MainViewModel(fakeRepository)
        viewModel.categories.observeForever { }
        setupScreenWith(viewModel, "non_existent_id")

        // Verify UI fields are empty, indicating no category was loaded
        composeTestRule.onNodeWithText("List Name").assert(hasText(""))
        composeTestRule.onAllNodes(hasParent(hasContentDescription("items"))).assertCountEquals(0)
    }

    @Test
    fun largeCategoryName_isAccepted() {
        // Setup screen and init long category name
        val veryLongName = "A".repeat(500)
        val fakeRepository = FakeRepository()
        setupScreenWith(MainViewModel(fakeRepository), null)

        // Type the very long name
        composeTestRule.onNodeWithText("List Name").performTextInput(veryLongName)

        // Verify that the typed text is in the field
        composeTestRule.onNodeWithText("List Name").assert(hasText(veryLongName))
    }

    @Test
    fun largeNumberOfItems_displayedAndBackedByViewModel() {
        // Setup screen and large number of items
        val largeNumberOfItems = 150
        val fakeRepository = FakeRepository()
        val viewModel = MainViewModel(fakeRepository)
        setupScreenWith(viewModel, null)

        // Add a large number of items
        repeat(largeNumberOfItems) {
            composeTestRule.onNodeWithContentDescription("Add").performClick()
        }

        // Verify that the correct number of items are in viewmodel
        assertEquals(largeNumberOfItems, viewModel.currentCategoryThings.size)

        // Verify that the add button is still displayed
        // Note: You cannot verify there are 150 items displayed in the LazyList due to the nature
        // how LazyLists populate views on the screen
        composeTestRule.onNodeWithContentDescription("Add").assertIsDisplayed()
    }

}
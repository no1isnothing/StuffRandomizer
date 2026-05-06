package com.thebipolaroptimist.stuffrandomizer.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thebipolaroptimist.stuffrandomizer.MainViewModel
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.fakes.FakeRepository
import com.thebipolaroptimist.stuffrandomizer.fakes.UUID_ONE
import com.thebipolaroptimist.stuffrandomizer.fakes.UUID_ZERO
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class PartyCreationScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val categories = listOf(
        Category(UUID_ZERO, "Category 1", listOf("Item 1", "Item 2")),
        Category(UUID_ONE, "Category 2", listOf("Item 3", "Item 4"))
    )

    private fun setupScreenWith(
        viewModel: MainViewModel,
        navigateBack: () -> Unit = {},
        toPartyEdit: (uuid: String) -> Unit = {}
    ) {
        composeTestRule.setContent {
            PartyCreationScreen(
                mainViewModel = viewModel,
                navigateBack = navigateBack,
                toPartyEdit = toPartyEdit
            )
        }
    }

    @Test
    fun init_withCategories_displayedCorrectly() = runTest {
        val repository = FakeRepository()
        repository.insertCategories(categories)
        val viewModel = MainViewModel(repository)
        setupScreenWith(viewModel)

        // Verify that the screen title 'Party Creation' is displayed.
        composeTestRule.onNodeWithText(context.getString(R.string.party_creation_label)).assertIsDisplayed()
        
        // Verify that the back arrow navigation icon is present.
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.back)).assertIsDisplayed()
        
        // Verify that the 'Roll' action icon is present.
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.roll)).assertIsDisplayed()
        
        // Verify that the 'Party Name' OutlinedTextField is displayed.
        composeTestRule.onNodeWithTag("party_name_field").assertIsDisplayed()
        
        // Verify that the 'Assignees' LabelText is displayed.
        composeTestRule.onNodeWithText(context.getString(R.string.assignees)).assertIsDisplayed()
        
        // Verify that the 'Assignments' LabelText is displayed.
        composeTestRule.onNodeWithText(context.getString(R.string.assignments)).assertIsDisplayed()
        
        // Verify that the categories are displayed in the LazyColumn via tags.
        composeTestRule.onNodeWithTag("assignment_item_Category 1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("assignment_item_Category 2").assertIsDisplayed()
    }

    @Test
    fun backNavigation_whenClicked_invokesNavigateBack() {
        var navigateBackCalled = false
        val viewModel = MainViewModel(FakeRepository())
        setupScreenWith(viewModel, navigateBack = { navigateBackCalled = true })

        // When the back arrow icon is clicked, verify that the `navigateBack` lambda is invoked.
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.back)).performClick()
        assertTrue(navigateBackCalled)
    }

    @Test
    fun partyNameInput_whenEntered_updatesViewModel() {
        val viewModel = MainViewModel(FakeRepository())
        setupScreenWith(viewModel)

        val testName = "My New Party"
        // Use test tag for unambiguous identification
        composeTestRule.onNodeWithTag("party_name_field").performTextInput(testName)
        assertEquals(testName, viewModel.newPartyName.value)
    }

    @Test
    fun assigneeSelection_whenSelected_updatesViewModel() = runTest {
        val repository = FakeRepository()
        repository.insertCategories(categories)
        val viewModel = MainViewModel(repository)
        setupScreenWith(viewModel)

        // Initial selection should be 0
        assertEquals(0, viewModel.newAssigneeSelection.value)

        // Open dropdown using tag
        composeTestRule.onNodeWithTag("assignee_dropdown").performClick()

        // Select second item in the dropdown menu.
        // We filter for the one in the popup.
        composeTestRule.onAllNodesWithText("Category 2")
            .filterToOne(hasAnyAncestor(isPopup()))
            .performClick()

        // Verify that `mainViewModel.newAssigneeSelection` is updated with the correct index.
        assertEquals(1, viewModel.newAssigneeSelection.value)
    }

    @Test
    fun assignmentItemChecking_whenToggled_updatesViewModel() = runTest {
        val repository = FakeRepository()
        repository.insertCategories(categories)
        val viewModel = MainViewModel(repository)
        setupScreenWith(viewModel)

        // Give it a moment to run LaunchedEffect
        composeTestRule.waitForIdle()

        // We target the toggleable element within the tagged row.
        val category1Checkbox = composeTestRule.onNode(isToggleable() and hasAnyAncestor(hasTestTag("assignment_item_Category 1")))

        // When a CheckableItem is checked, verify that `mainViewModel.newPartyCheckedState` is updated.
        category1Checkbox.performClick()
        assertTrue(viewModel.newPartyCheckedState.value[0])

        // When unchecked, verify it is false.
        category1Checkbox.performClick()
        assertEquals(false, viewModel.newPartyCheckedState.value[0])
    }

    @Test
    fun rollAction_emptyPartyName_showsSnackbar() {
        val viewModel = MainViewModel(FakeRepository())
        var toPartyEditCalled = false
        setupScreenWith(viewModel, toPartyEdit = { toPartyEditCalled = true })

        // Given `mainViewModel.newPartyName` is empty,
        // when the 'Roll' action icon is clicked,
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.roll)).performClick()

        // verify that a Snackbar with the message R.string.warning_match_name_empty is shown.
        composeTestRule.onNodeWithText(context.getString(R.string.warning_match_name_empty)).assertIsDisplayed()
        
        // Verify that `toPartyEdit` is NOT called.
        assertTrue(!toPartyEditCalled)
    }

    @Test
    fun rollAction_validInput_navigatesToEdit() = runTest {
        val repository = FakeRepository()
        repository.insertCategories(categories)
        val viewModel = MainViewModel(repository)
        var toPartyEditUid: String? = null
        setupScreenWith(viewModel, toPartyEdit = { toPartyEditUid = it })

        composeTestRule.waitForIdle()

        // Given `mainViewModel.newPartyName` is not empty,
        val testName = "Test Party"
        composeTestRule.onNodeWithTag("party_name_field").performTextInput(testName)

        // and some assignments are checked
        composeTestRule.onNode(isToggleable() and hasAnyAncestor(hasTestTag("assignment_item_Category 1")))
            .performClick()

        // when the 'Roll' action icon is clicked,
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.roll)).performClick()

        // Verify that `toPartyEdit` is called with the UID of the created party.
        assertNotNull(toPartyEditUid)
    }

    @Test
    fun rollAction_emptyAssignments_showsSnackbar() = runTest {
        val repository = FakeRepository()
        repository.insertCategories(categories)
        val viewModel = MainViewModel(repository)
        var toPartyEditCalled = false
        setupScreenWith(viewModel, toPartyEdit = { toPartyEditCalled = true })

        composeTestRule.waitForIdle()

        // Given `mainViewModel.newPartyName` is not empty,
        val testName = "Test Party"
        composeTestRule.onNodeWithTag("party_name_field").performTextInput(testName)

        // but NO assignments are checked
        
        // when the 'Roll' action icon is clicked,
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.roll)).performClick()

        // Verify that a Snackbar with the message R.string.warning_match_assignments_empty is shown.
        composeTestRule.onNodeWithText(context.getString(R.string.warning_match_assignments_empty)).assertIsDisplayed()
        
        // Verify that `toPartyEdit` is NOT called.
        assertTrue(!toPartyEditCalled)
    }

    @Test
    fun init_onComposition_setsUpViewModelState() = runTest {
        val repository = FakeRepository()
        repository.insertCategories(categories)
        val viewModel = MainViewModel(repository)
        
        setupScreenWith(viewModel)
        
        // Verify that `mainViewModel.setupNewPartyState(categoryList.size)` is called when the composable is first displayed.
        composeTestRule.waitForIdle()
        assertEquals(categories.size, viewModel.newPartyCheckedState.value.size)
    }

    @Test
    fun init_withEmptyCategories_displaysNoAssignments() = runTest {
        val repository = FakeRepository()
        val viewModel = MainViewModel(repository)
        setupScreenWith(viewModel)

        composeTestRule.waitForIdle()

        // Given `mainViewModel.categories` LiveData emits an empty list,
        // verify that the Assignments LazyColumn is empty.
        composeTestRule.onNodeWithTag("assignment_item_Category 1").assertDoesNotExist()
        
        // Verify that `mainViewModel.setupNewPartyState(0)` is called.
        assertEquals(0, viewModel.newPartyCheckedState.value.size)
    }

    @Test
    fun categories_whenUpdated_updatesUi() = runTest {
        val repository = FakeRepository()
        val viewModel = MainViewModel(repository)
        setupScreenWith(viewModel)

        composeTestRule.waitForIdle()
        assertEquals(0, viewModel.newPartyCheckedState.value.size)

        // When `mainViewModel.categories` LiveData emits a new list of categories after initial composition,
        repository.insertCategories(categories)
        composeTestRule.waitForIdle()
        
        // Verify that the Assignees DropDownText updates its items.
        composeTestRule.onNodeWithTag("assignee_dropdown").performClick()
        
        // Items should be displayed in the dropdown menu.
        composeTestRule.onAllNodesWithText("Category 1")
            .filterToOne(hasAnyAncestor(isPopup()))
            .assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Category 2")
            .filterToOne(hasAnyAncestor(isPopup()))
            .assertIsDisplayed()
        
        // Close dropdown
        composeTestRule.onAllNodesWithText("Category 1")
            .filterToOne(hasAnyAncestor(isPopup()))
            .performClick()

        // Verify that the Assignments LazyColumn updates its items.
        composeTestRule.onNodeWithTag("assignment_item_Category 1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("assignment_item_Category 2").assertIsDisplayed()
        
        // Verify that `mainViewModel.setupNewPartyState` is called again with the new list size.
        assertEquals(2, viewModel.newPartyCheckedState.value.size)
    }

    @Test
    fun partyNameInput_longName_updatesViewModel() {
        val viewModel = MainViewModel(FakeRepository())
        setupScreenWith(viewModel)

        val longName = "A".repeat(1000)
        composeTestRule.onNodeWithTag("party_name_field").performTextInput(longName)
        assertEquals(longName, viewModel.newPartyName.value)
        composeTestRule.onNodeWithText(longName).assertIsDisplayed()
    }

    @Test
    fun assignmentsList_withManyCategories_scrollsToLastItem() = runTest {
        val repository = FakeRepository()
        val manyCategories = List(50) { i -> Category(UUID.randomUUID(), "Category $i", listOf("Item")) }
        repository.insertCategories(manyCategories)
        val viewModel = MainViewModel(repository)
        setupScreenWith(viewModel)

        composeTestRule.waitForIdle()

        // Verify the first is displayed
        composeTestRule.onNodeWithTag("assignment_item_Category 0").assertIsDisplayed()

        // Scroll to the last item using the list's test tag and then verify it's displayed
        composeTestRule.onNodeWithTag("assignments_list")
            .performScrollToNode(hasTestTag("assignment_item_Category 49"))
        
        composeTestRule.onNodeWithTag("assignment_item_Category 49").assertIsDisplayed()
    }
}

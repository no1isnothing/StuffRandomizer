package com.thebipolaroptimist.stuffrandomizer.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.fakes.FakeRepository
import com.thebipolaroptimist.stuffrandomizer.fakes.UUID_ZERO
import com.thebipolaroptimist.stuffrandomizer.viewmodel.PartyEditViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PartyEditScreenKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private fun setupScreenWith(
        viewModel: PartyEditViewModel,
        id: String,
        navigateBack: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            PartyEditScreen(viewModel = viewModel, id = id, navigateBack = navigateBack)
        }
    }

    private fun repositoryWithParty(): Pair<FakeRepository, Party> {
        val assignees = Category(UUID_ZERO, "Assignees", listOf("Jane", "Bear"))
        val party = Party(
            UUID_ZERO,
            "Party 0",
            arrayListOf(Member("Jane", hashMapOf(Pair("Assignees", "Bear")))),
            "Assignees"
        )
        val repository = FakeRepository().apply {
            categoryMap[assignees.uid] = assignees
            partyMap[party.uid] = party
        }
        return repository to party
    }

    @Test
    fun init_loadsPartyNameAndMembers() {
        val (repository, party) = repositoryWithParty()

        setupScreenWith(PartyEditViewModel(repository), id = party.uid.toString())

        composeTestRule.onNodeWithText("Party 0").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jane").assertIsDisplayed()
    }

    @Test
    fun clickingSave_navigatesBack() {
        val (repository, party) = repositoryWithParty()
        var backCalled = false

        setupScreenWith(
            PartyEditViewModel(repository),
            id = party.uid.toString(),
            navigateBack = { backCalled = true }
        )

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.save))
            .performClick()

        assert(backCalled)
    }
}

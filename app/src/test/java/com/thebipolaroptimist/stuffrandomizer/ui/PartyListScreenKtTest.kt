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
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.fakes.FakeRepository
import com.thebipolaroptimist.stuffrandomizer.fakes.UUID_ONE
import com.thebipolaroptimist.stuffrandomizer.fakes.UUID_ZERO
import com.thebipolaroptimist.stuffrandomizer.viewmodel.PartyListViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PartyListScreenKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    private fun setupScreenWith(
        viewModel: PartyListViewModel,
        toPartyCreation: () -> Unit = {},
        toPartyEdit: (uuid: String) -> Unit = {},
    ) {
        composeTestRule.setContent {
            PartyListScreen(
                viewModel = viewModel,
                toPartyCreation = toPartyCreation,
                toPartyEdit = toPartyEdit
            )
        }
    }

    @Test
    fun init_withEmptyList_noItemsDisplayed() {
        setupScreenWith(PartyListViewModel(FakeRepository()))

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.add_match))
            .assertIsDisplayed()
    }

    @Test
    fun init_withParties_listDisplayed() {
        val party = Party(UUID_ZERO, "Party 0", arrayListOf(Member("A", hashMapOf())), "Assignees")
        val party1 = Party(UUID_ONE, "Party 1", arrayListOf(Member("B", hashMapOf())), "Assignees")
        val repository = FakeRepository().apply {
            partyMap[party.uid] = party
            partyMap[party1.uid] = party1
        }

        setupScreenWith(PartyListViewModel(repository))

        composeTestRule.onNodeWithText("Party 0").assertIsDisplayed()
        composeTestRule.onNodeWithText("Party 1").assertIsDisplayed()
    }

    @Test
    fun clickingParty_navigatesToPartyEdit() {
        val party = Party(UUID_ZERO, "Party 0", arrayListOf(Member("A", hashMapOf())), "Assignees")
        val repository = FakeRepository().apply {
            partyMap[party.uid] = party
        }
        var editedUuid: String? = null

        setupScreenWith(PartyListViewModel(repository), toPartyEdit = { editedUuid = it })

        composeTestRule.onNodeWithText("Party 0").performClick()

        assert(editedUuid == UUID_ZERO.toString())
    }
}

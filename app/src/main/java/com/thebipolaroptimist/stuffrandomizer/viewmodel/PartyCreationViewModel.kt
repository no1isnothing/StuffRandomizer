package com.thebipolaroptimist.stuffrandomizer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Repository
import com.thebipolaroptimist.stuffrandomizer.utilties.Parties
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * A [ViewModel] for [com.thebipolaroptimist.stuffrandomizer.ui.PartyCreationScreen].
 */
@HiltViewModel
class PartyCreationViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    data class UiState(
        val categories: List<Category> = emptyList(),
        val newPartyName: String = "",
        val checkedState: List<Boolean> = emptyList(),
        val assigneeSelection: Int = 0,
    )

    sealed interface SaveResult {
        data class Created(val partyId: String) : SaveResult
        data object NameRequired : SaveResult
        data object SelectionRequired : SaveResult
    }

    private val categories: StateFlow<List<Category>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val newPartyName = MutableStateFlow("")
    private val checkedState = MutableStateFlow<List<Boolean>>(emptyList())
    private val assigneeSelection = MutableStateFlow(0)

    val uiState: StateFlow<UiState> = combine(
        categories, newPartyName, checkedState, assigneeSelection
    ) { categories, name, checked, assignee ->
        UiState(categories, name, checked, assignee)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    fun onNameChange(name: String) {
        newPartyName.value = name
    }

    fun onAssigneeSelected(index: Int) {
        assigneeSelection.value = index
    }

    fun onCategoryChecked(index: Int, checked: Boolean) {
        val current = checkedState.value
        if (index in current.indices) {
            checkedState.value = current.toMutableList().apply { set(index, checked) }
        }
    }

    /**
     * Ensure [checkedState] has one entry per category, defaulting to unchecked.
     * Does nothing once [checkedState] has already been sized.
     */
    fun ensureCheckedStateSized(size: Int) {
        if (checkedState.value.isEmpty() && size > 0) {
            checkedState.value = List(size) { false }
        }
    }

    /**
     * Build and save a new [com.thebipolaroptimist.stuffrandomizer.data.Party] from the current
     * form state. Suspends until the party is persisted, so callers can safely navigate to it
     * (e.g. a party edit screen) once this returns.
     */
    suspend fun createParty(): SaveResult {
        if (newPartyName.value.isEmpty()) {
            return SaveResult.NameRequired
        }

        val party = Parties.buildParty(
            newPartyName.value,
            assigneeSelection.value,
            checkedState.value,
            categories.value
        ) ?: return SaveResult.SelectionRequired

        repository.insertParty(party)
        resetForm()
        return SaveResult.Created(party.uid.toString())
    }

    private fun resetForm() {
        newPartyName.value = ""
        checkedState.value = emptyList()
        assigneeSelection.value = 0
    }
}

package com.thebipolaroptimist.stuffrandomizer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.data.Repository
import com.thebipolaroptimist.stuffrandomizer.utilties.Parties
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A [ViewModel] for [com.thebipolaroptimist.stuffrandomizer.ui.PartyEditScreen].
 */
@HiltViewModel
class PartyEditViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val logger: FluentLogger = FluentLogger.forEnclosingClass()

    data class UiState(
        val partyId: String? = null,
        val partyName: String = "",
        val members: List<Member> = emptyList(),
    )

    private var party: Party? = null

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Load the [Party] with [partyId] into [uiState]. Does nothing if already loaded.
     */
    fun load(partyId: String) {
        if (partyId == _uiState.value.partyId) {
            return
        }
        viewModelScope.launch {
            val loadedParty =
                repository.getAllParties().first().firstOrNull { it.uid.toString() == partyId }
                    ?: return@launch

            party = loadedParty
            _uiState.value = UiState(partyId, loadedParty.partyName, loadedParty.members.toList())
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(partyName = name)
    }

    /**
     * Redo the randomization for things in the [Category][com.thebipolaroptimist.stuffrandomizer.data.Category]s
     * assigned to the loaded [Party].
     */
    fun reroll() {
        val currentParty = party ?: return
        viewModelScope.launch {
            val categories = repository.getCategoriesByName(currentParty.getAllCategoryNames())
            val assignees = repository.getCategoryByName(currentParty.assigneeList)

            if (assignees == null) {
                logger.atWarning().log("Category %s not found", currentParty.assigneeList)
                return@launch
            }

            _uiState.value =
                _uiState.value.copy(members = Parties.roll(assignees.things, categories))
        }
    }

    /**
     * Save the loaded [Party] with the current [uiState].
     */
    fun save() {
        val currentParty = party ?: return
        currentParty.apply {
            partyName = _uiState.value.partyName
            members.clear()
            members.addAll(_uiState.value.members)
        }
        logger.atInfo().log("Inserting edited party %s", currentParty)
        viewModelScope.launch {
            repository.insertParty(currentParty)
        }
    }
}

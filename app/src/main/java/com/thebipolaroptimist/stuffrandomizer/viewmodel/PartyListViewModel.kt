package com.thebipolaroptimist.stuffrandomizer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * A [ViewModel] for [com.thebipolaroptimist.stuffrandomizer.ui.PartyListScreen].
 */
@HiltViewModel
class PartyListViewModel @Inject constructor(repository: Repository) : ViewModel() {

    data class UiState(val parties: List<Party> = emptyList())

    val uiState: StateFlow<UiState> = repository.getAllParties()
        .map { UiState(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, UiState())
}

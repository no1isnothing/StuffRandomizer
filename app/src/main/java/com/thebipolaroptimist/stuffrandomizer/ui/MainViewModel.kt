package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.thebipolaroptimist.stuffrandomizer.data.Stuff
import com.thebipolaroptimist.stuffrandomizer.data.MainRepository
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.google.common.flogger.FluentLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A [ViewModel] for interactions between the [MainActivity] and it's Fragment
 * and the [MainRepository].
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {
    private val logger: FluentLogger = FluentLogger.forEnclosingClass()

    val parties = mainRepository.getAllParties().asLiveData()
    val stuffs = mainRepository.getAllStuff().asLiveData()
    var inProgressStuff: Stuff? = null
    var inProgressPartyName: String? = null
    var inProgressCheckBoxState: List<Boolean>? = null
    var inProgressAssigneeSelection: String? = null

    private fun clearInProgressMatchState() {
        inProgressPartyName = null
        inProgressCheckBoxState = null
        inProgressAssigneeSelection = null
    }

    fun insertParty(party: Party) {
        viewModelScope.launch {
            mainRepository.insertParty(party)
        }
    }

    fun insertStuff(stuff: Stuff) {
        viewModelScope.launch {
            clearInProgressMatchState()
            mainRepository.insertStuff(stuff)
        }
    }

    fun deleteAllStuff() {
        viewModelScope.launch {
            clearInProgressMatchState()
            mainRepository.deleteAllStuff()
        }
    }

    fun deleteParties() {
        viewModelScope.launch {
            mainRepository.deleteAllParties()
        }
    }
}
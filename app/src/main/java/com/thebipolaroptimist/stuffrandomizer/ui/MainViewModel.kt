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

    val matches = mainRepository.getAllPairingGroups().asLiveData()
    val itemLists = mainRepository.getAllStuff().asLiveData()
    var inProgressStuff: Stuff? = null
    var inProgressMatchName: String? = null
    var inProgressCheckBoxState: List<Boolean>? = null
    var inProgressAssigneeSelection: String? = null

    private fun clearInProgressMatchState() {
        inProgressMatchName = null
        inProgressCheckBoxState = null
        inProgressAssigneeSelection = null
    }

    fun insertMatchSet(party: Party) {
        viewModelScope.launch {
            mainRepository.insertPairingGroup(party)
        }
    }

    fun insertItemList(stuff: Stuff) {
        viewModelScope.launch {
            clearInProgressMatchState()
            mainRepository.insertStuff(stuff)
        }
    }

    fun deleteItemLists() {
        viewModelScope.launch {
            clearInProgressMatchState()
            mainRepository.deleteAllStuff()
        }
    }

    fun deleteMatchSets() {
        viewModelScope.launch {
            mainRepository.deleteAllPairingGroups()
        }
    }
}
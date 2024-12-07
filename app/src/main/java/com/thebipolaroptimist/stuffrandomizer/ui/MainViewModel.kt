package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.thebipolaroptimist.stuffrandomizer.data.ItemList
import com.thebipolaroptimist.stuffrandomizer.data.MatchRepository
import com.thebipolaroptimist.stuffrandomizer.data.MatchSet
import com.google.common.flogger.FluentLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A [ViewModel] for interactions between the [MainActivity] and it's Fragment
 * and the [MatchRepository].
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val matchRepository: MatchRepository) : ViewModel() {
    private val logger: FluentLogger = FluentLogger.forEnclosingClass()

    val matches = matchRepository.getAllMatchSets().asLiveData()
    val itemLists = matchRepository.getAllItemLists().asLiveData()
    var inProgressItemList: ItemList? = null
    var inProgressMatchName: String? = null
    var inProgressCheckBoxState: List<Boolean>? = null
    var inProgressAssigneeSelection: String? = null

    private fun clearInProgressMatchState() {
        inProgressMatchName = null
        inProgressCheckBoxState = null
        inProgressAssigneeSelection = null
    }

    fun insertMatchSet(matchSet: MatchSet) {
        viewModelScope.launch {
            matchRepository.insertMatchSet(matchSet)
        }
    }

    fun insertItemList(itemList: ItemList) {
        viewModelScope.launch {
            clearInProgressMatchState()
            matchRepository.insertItemList(itemList)
        }
    }

    fun deleteItemLists() {
        viewModelScope.launch {
            clearInProgressMatchState()
            matchRepository.deleteAllItemLists()
        }
    }

    fun deleteMatchSets() {
        viewModelScope.launch {
            matchRepository.deleteAllMatchSets()
        }
    }
}
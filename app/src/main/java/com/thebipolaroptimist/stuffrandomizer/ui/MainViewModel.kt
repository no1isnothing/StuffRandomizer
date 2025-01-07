package com.thebipolaroptimist.stuffrandomizer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.thebipolaroptimist.stuffrandomizer.data.Category
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
    val categories = mainRepository.getAllCategories().asLiveData()
    var inProgressCategory: Category? = null
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

    fun insertCategory(category: Category) {
        viewModelScope.launch {
            clearInProgressMatchState()
            mainRepository.insertCategory(category)
        }
    }

    fun insertCategories(categories: List<Category>) {
        viewModelScope.launch {
            clearInProgressMatchState()
            mainRepository.insertCategories(categories)
        }
    }

    suspend fun getCategoriesByName(categoryNames: List<String>): List<Category> =
        mainRepository.getCategoriesByName(categoryNames)

    suspend fun getCategoryByName(categoryName: String) =
        mainRepository.getCategoryByName(categoryName)

    fun deleteAllCategories() {
        viewModelScope.launch {
            clearInProgressMatchState()
            mainRepository.deleteAllCategories()
        }
    }

    fun deleteParties() {
        viewModelScope.launch {
            mainRepository.deleteAllParties()
        }
    }
}
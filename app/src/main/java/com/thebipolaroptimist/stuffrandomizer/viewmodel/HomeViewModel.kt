package com.thebipolaroptimist.stuffrandomizer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Repository
import com.thebipolaroptimist.stuffrandomizer.utilties.SampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A [ViewModel] for [com.thebipolaroptimist.stuffrandomizer.ui.HomeScreen].
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    data class UiState(val categories: List<Category> = emptyList())

    val uiState: StateFlow<UiState> = repository.getAllCategories()
        .map { UiState(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    /**
     * Insert the default BG categories and a set of sample [Category]/[com.thebipolaroptimist.stuffrandomizer.data.Party] data.
     */
    fun addSampleData() {
        viewModelScope.launch {
            val races = context.resources.getStringArray(R.array.bg_race).toList()
            val backgrounds = context.resources.getStringArray(R.array.bg_background).toList()
            val classes = context.resources.getStringArray(R.array.bg_char_class).toList()

            repository.insertCategories(
                SampleData.createDefaultCategories(races, backgrounds, classes)
            )
            repository.insertParty(SampleData.createSamplePartyData())
            repository.insertCategories(SampleData.createSampleCategoryData())
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllCategories()
            repository.deleteAllParties()
        }
    }
}

package com.thebipolaroptimist.stuffrandomizer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thebipolaroptimist.stuffrandomizer.R
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Repository
import com.thebipolaroptimist.stuffrandomizer.utilties.CategoryEditing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * A [ViewModel] for [com.thebipolaroptimist.stuffrandomizer.ui.CategoryDetailsScreen].
 */
@HiltViewModel
class CategoryDetailsViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    data class UiState(
        val categoryId: String? = null,
        val currentCategoryName: String = "",
        val currentCategoryThings: List<String> = emptyList(),
    )

    sealed interface BackAction {
        data object Save : BackAction
        data object DeleteEmpty : BackAction
        data class Warn(val messageRes: Int) : BackAction
    }

    private var originalCategory: Category = Category(UUID.randomUUID(), "", listOf())
    private var hasLoaded = false

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Load the [Category] with [categoryId] into [uiState], or a blank [Category] if `null`.
     * Does nothing if already loaded.
     */
    fun load(categoryId: String?) {
        if (hasLoaded && categoryId == _uiState.value.categoryId) {
            return
        }
        hasLoaded = true
        viewModelScope.launch {
            val category = categoryId?.let { id ->
                repository.getAllCategories().first().firstOrNull { it.uid.toString() == id }
            } ?: Category(UUID.randomUUID(), "", listOf())

            originalCategory = category
            _uiState.value = UiState(categoryId, category.name, category.things)
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(currentCategoryName = name)
    }

    fun onThingChange(index: Int, value: String) {
        val things = _uiState.value.currentCategoryThings
        if (index in things.indices) {
            _uiState.value = _uiState.value.copy(
                currentCategoryThings = things.toMutableList().apply { set(index, value) }
            )
        }
    }

    fun addThing() {
        _uiState.value =
            _uiState.value.copy(currentCategoryThings = _uiState.value.currentCategoryThings + "")
    }

    fun removeThing(value: String) {
        _uiState.value = _uiState.value.copy(
            currentCategoryThings = _uiState.value.currentCategoryThings.toMutableList()
                .apply { remove(value) }
        )
    }

    /**
     * Reset [uiState] back to the values the [Category] was loaded with.
     */
    fun resetToOriginal() {
        _uiState.value = _uiState.value.copy(
            currentCategoryName = originalCategory.name,
            currentCategoryThings = originalCategory.things
        )
    }

    fun deleteCategory() {
        viewModelScope.launch {
            repository.deleteCategory(originalCategory)
        }
    }

    /**
     * Decide what should happen when the screen is backed out of, performing the resulting
     * save/delete side effect.
     */
    fun onBackRequested(): BackAction {
        val state = _uiState.value
        return when (
            CategoryEditing.resolveBackAction(state.currentCategoryName, state.currentCategoryThings)
        ) {
            CategoryEditing.SaveOutcome.DELETE_EMPTY -> {
                deleteCategory()
                BackAction.DeleteEmpty
            }

            CategoryEditing.SaveOutcome.WARN_NO_ITEMS ->
                BackAction.Warn(R.string.warning_empty_item_list)

            CategoryEditing.SaveOutcome.WARN_NO_NAME ->
                BackAction.Warn(R.string.warning_list_name_empty)

            CategoryEditing.SaveOutcome.SAVE -> {
                save()
                BackAction.Save
            }
        }
    }

    private fun save() {
        val state = _uiState.value
        originalCategory.name = state.currentCategoryName
        originalCategory.things = CategoryEditing.sanitizeThings(state.currentCategoryThings)
        viewModelScope.launch {
            repository.insertCategory(originalCategory)
        }
    }
}

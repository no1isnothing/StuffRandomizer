package com.thebipolaroptimist.stuffrandomizer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.MainRepository
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.utilties.Parties
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A [ViewModel] for interactions between the [MainActivity], screens, and the [MainRepository].
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {
    private val logger: FluentLogger = FluentLogger.forEnclosingClass()

    val parties = mainRepository.getAllParties().asLiveData()
    val categories = mainRepository.getAllCategories().asLiveData()
    
    var currentCategoryName by mutableStateOf("")
    var currentCategoryThings = mutableStateListOf<String>()
    var editPartyName by mutableStateOf("")
    var editPartyMembers = mutableStateListOf<Member>()
    var editPartyUuid by mutableStateOf("")
    var newPartyName by mutableStateOf("")
    var newPartyCheckedState = mutableStateListOf<Boolean>()
    var newAssigneeSelection by mutableIntStateOf(0)

    /**
     * Reset data for [Party] being created.
     */
    private fun resetNewParty() {
        newPartyName = ""
        newPartyCheckedState.clear()
        newAssigneeSelection = 0
    }

    /**
     * Reset data for [Party] being edited.
     */
    fun resetEditedParty() {
        editPartyMembers.clear()
        editPartyName = ""
        editPartyUuid = ""
    }

    /**
     * Insert a [Party] into the [MainRepository].
     *
     * @param party The party to insert
     */
    fun insertParty(party: Party) {
        viewModelScope.launch {
            mainRepository.insertParty(party)
        }
    }

    /**
     * Insert a [Category] into the [MainRepository].
     *
     * @param category The category to insert.
     */
    fun insertCategory(category: Category) {
        viewModelScope.launch {
            resetNewParty()
            mainRepository.insertCategory(category)
        }
    }

    /**
     *  Remove [Category] from [MainRepository]
     *
     *  @param category The category to delete.
     */
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            mainRepository.deleteCategory(category)
        }
    }

    /**
     * Insert [List] of [Category]s into [MainRepository].
     *
     * @param categories List of categories to insert.
     */
    fun insertCategories(categories: List<Category>) {
        viewModelScope.launch {
            resetNewParty()
            mainRepository.insertCategories(categories)
        }
    }

    /**
     * Update [currentCategoryThings] at [position] with [thing].
     *
     * @param position The position to update
     * @param thing The string to put at [position]
     */
    fun updateCurrentCategoryThings(position: Int, thing: String) {
        logger.atInfo().log("Updating %d to %s", position, thing)
        currentCategoryThings[position] = thing
    }

    /**
     * Setup [currentCategoryThings] and [currentCategoryName] using [category]
     *
     * @param category The [Category] to use to fill out current category variables
     */
    fun setupCurrentCategory(category: Category?) {
        currentCategoryThings.clear()

        category?.let {
            currentCategoryThings.addAll(it.things)
            currentCategoryName = it.name
        }
    }

    /**
     * Delete all [Category]s from [MainRepository].
     */
    fun deleteAllCategories() {
        viewModelScope.launch {
            resetNewParty()
            mainRepository.deleteAllCategories()
        }
    }

    /**
     * Delete all [Party]s from [MainRepository].
     */
    fun deleteAllParties() {
        viewModelScope.launch {
            mainRepository.deleteAllParties()
        }
    }

    /**
     * Setup [newPartyCheckedState]
     *
     * If it hasn't been initialized, this will make it an array of [size] filled with false.
     * If it's already initialized, this does nothing.
     */
    fun setupNewPartyState(size: Int) {
        if(newPartyCheckedState.size == 0) {
            newPartyCheckedState.addAll(Array(size) { false })
        }
    }
    /**
     * Create and save [Party] with new party variables.
     *
     * Use the [newAssigneeSelection] and [newPartyCheckedState] to gather data for making a new
     * [Party]. Then make the new [Party] and insert into [MainRepository].
     *
     */
    fun createAndSaveNewParty(
        categoryList: List<Category>,
    ): Party? {
        val selectedCategoryList = ArrayList<Category>()
        val assigneeList = categoryList[newAssigneeSelection]

        for ((index, checkBoxState) in newPartyCheckedState.withIndex()) {
            if (checkBoxState) {
                selectedCategoryList.add(categoryList[index])
            }
        }
        if (selectedCategoryList.isEmpty()) {
            return null
        }

        val party = Parties.create(newPartyName, assigneeList, selectedCategoryList)
        insertParty(party)
        resetNewParty()
        return party
    }

    /**
     *  Redo the randomization for things in [Category]s assigned to [editPartyMembers].
     */
    fun rerollEditPartyMembers(party: Party) {
        viewModelScope.launch {
            val categories = getCategoriesByName(party.getAllCategoryNames())
            val assignees = getCategoryByName(party.assigneeList)

            if (assignees == null) {
                logger.atWarning().log("Category %s not found", party.assigneeList)
                return@launch
            }

            editPartyMembers.clear()
            editPartyMembers.addAll(Parties.roll(assignees.things, categories))
        }
    }

    /**
     * Save [party] with [editPartyMembers] and [editPartyName].
     *
     * @param party Party to save with edit variables.
     */
    fun saveEditedParty(party: Party) {
        party.apply {
            partyName = editPartyName
            members.clear()
            members.addAll(editPartyMembers)
        }
        logger.atInfo().log("Inserting edited party %s", party)
        insertParty(party)
        resetEditedParty()
    }

    /**
     *  Set edit party variables from [party]
     *
     *  @param party Party to use to set edit variables.
     */
    fun loadEditParty(party: Party) {
        logger.atInfo().log("Loading information for party %s", party)

        editPartyMembers.clear()
        editPartyMembers.addAll(party.members)
        editPartyName = party.partyName
        editPartyUuid = party.uid.toString()

    }

    private suspend fun getCategoriesByName(categoryNames: List<String>): List<Category> =
        mainRepository.getCategoriesByName(categoryNames)

    private suspend fun getCategoryByName(categoryName: String) =
        mainRepository.getCategoryByName(categoryName)
}
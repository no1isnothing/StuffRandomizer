package com.thebipolaroptimist.stuffrandomizer.ui

import android.icu.text.Transliterator.Position
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.MainRepository
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.google.common.flogger.FluentLogger
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.utilties.Parties
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.log

/**
 * A [ViewModel] for interactions between the [MainActivity], screens, and the [MainRepository].
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {
    private val logger: FluentLogger = FluentLogger.forEnclosingClass()

    val parties = mainRepository.getAllParties().asLiveData()
    val categories = mainRepository.getAllCategories().asLiveData()
    var newCategoryName by mutableStateOf("")
    var newThing by mutableStateOf("")
    var newThings = arrayListOf<String>()
    var editCategoryName by mutableStateOf("")
    var editThings = mutableStateListOf<String>()
    var editPartyName by mutableStateOf("")
    var editPartyMembers = mutableStateListOf<Member>()
    var editPartyUuid by mutableStateOf("")
    var newPartyName by mutableStateOf("")
    var newPartyCheckedSate = mutableStateListOf<Boolean>()
    var newAssigneeSelection by mutableStateOf(0)

    /**
     * Reset data for [Party] being created.
     */
    fun resetNewParty() {
        newPartyName = ""
        newPartyCheckedSate.clear()
        newAssigneeSelection = 0
    }

    /**
     * Reset data for [Category] being created.
     */
    fun resetNewCategory() {
        newCategoryName = ""
        newThing = ""
        newThings.clear()
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

    fun updateEditThings(position: Int, thing: String) {
        logger.atInfo().log("Updating %d to %s", position, thing)
        editThings[position] = thing
    }

    /**
     * Create and save [Category] using new category variables.
     *
     * This functions uses the [newCategoryName] and [newThings]
     * to create a new [Category] object. Then inserts the [Category]
     * into the [MainRepository] and resets the new category and new party variables.
     *
     * @return false if [newCategoryName] or [newThings] is empty and insert isn't attempted,
     *         true otherwise.
     */
    fun saveNewCategory(): Boolean {
        if(newCategoryName.isEmpty() || newThings.isEmpty()) {
            return false
        }
        viewModelScope.launch {
            mainRepository.insertCategory(
                Category(
                    UUID.randomUUID(),
                    newCategoryName,
                    newThings
                )
            )
            resetNewCategory()
            resetNewParty()
        }
        return true
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
     * Delete all [Party]s from [MainRepository]/
     */
    fun deleteAllParties() {
        viewModelScope.launch {
            mainRepository.deleteAllParties()
        }
    }

    /**
     * Create and save [Party] with new party variables.
     *
     * Use the [newAssigneeSelection] and [newPartyCheckedSate] to gather data for making a new
     * [Party]. Then make the new [Party] and insert into [MainRepository].
     *
     */
    fun createAndSaveNewParty(
        categoryList: List<Category>,
    ): Boolean {
        val selectedCategoryList = ArrayList<Category>()
        val assigneeList = categoryList[newAssigneeSelection]

        for ((index, checkBoxState) in newPartyCheckedSate.withIndex()) {
            if (checkBoxState) {
                selectedCategoryList.add(categoryList[index])
            }
        }
        if (selectedCategoryList.isEmpty()) {
            return false
        }

        val party = Parties.create(newPartyName, assigneeList, selectedCategoryList)
        insertParty(party)
        resetNewParty()
        return true
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
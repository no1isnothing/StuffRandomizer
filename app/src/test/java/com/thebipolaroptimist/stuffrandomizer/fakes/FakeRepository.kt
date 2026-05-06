package com.thebipolaroptimist.stuffrandomizer.fakes

import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.data.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.UUID

const val UUID_ZERO_STRING = "00000000-0000-0000-0000-000000000000"
val UUID_ZERO: UUID = UUID.fromString(UUID_ZERO_STRING)
const val UUID_ONE_STRING = "00000000-0000-0000-0000-000000000001"
val UUID_ONE: UUID = UUID.fromString(UUID_ONE_STRING)
const val UUID_TWO_STRING = "00000000-0000-0000-0000-000000000002"
val UUID_TWO: UUID = UUID.fromString(UUID_TWO_STRING)

/**
 * A fake implementation of the [Repository] interface for testing purposes.
 *
 * This class uses [MutableStateFlow] to store data in memory, simulating a reactive
 * database. It follows the pattern of having a private mutable state and a public
 * read-only state flow.
 */
class FakeRepository : Repository {
    private val _categoryMap = MutableStateFlow<Map<UUID, Category>>(emptyMap())
    val categoryMap: StateFlow<Map<UUID, Category>> = _categoryMap.asStateFlow()

    private val _partyMap = MutableStateFlow<Map<UUID, Party>>(emptyMap())
    val partyMap: StateFlow<Map<UUID, Party>> = _partyMap.asStateFlow()

    override fun getAllParties(): Flow<List<Party>> {
        return _partyMap.map { it.values.toList() }
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return _categoryMap.map { it.values.toList() }
    }

    override suspend fun getCategoriesByName(names: List<String>): List<Category> {
        return _categoryMap.value.values.filter { names.contains(it.name) }
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return _categoryMap.value.values.firstOrNull { it.name == name }
    }

    override suspend fun insertParty(party: Party) {
        _partyMap.update { it + (party.uid to party) }
    }

    override suspend fun insertCategory(category: Category) {
        _categoryMap.update { it + (category.uid to category) }
    }

    override suspend fun deleteCategory(category: Category) {
        _categoryMap.update { it - category.uid }
    }

    override suspend fun insertCategories(categories: List<Category>) {
        _categoryMap.update { it + categories.associateBy { category -> category.uid } }
    }

    override suspend fun deleteAllParties() {
        _partyMap.value = emptyMap()
    }

    override suspend fun deleteAllCategories() {
        _categoryMap.value = emptyMap()
    }
}

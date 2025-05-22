package com.thebipolaroptimist.stuffrandomizer.fakes

import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Party
import com.thebipolaroptimist.stuffrandomizer.data.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
 * This class uses in-memory maps to store data, simulating a database or other
 * data source without actual persistence. It provides basic implementations
 * for the methods defined in the [Repository] interface.
 */
class FakeRepository : Repository {
    val categoryMap = mutableMapOf<UUID, Category>()
    val partyMap = mutableMapOf<UUID, Party>()
    override fun getAllParties(): Flow<List<Party>> {
        return flowOf(partyMap.entries.map { it.value })
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return flowOf(categoryMap.entries.map { it.value })
    }

    override suspend fun getCategoriesByName(names: List<String>): List<Category> {
        return categoryMap.values.filter { names.contains(it.name) }
    }

    override suspend fun getCategoryByName(name: String): Category? {
        return categoryMap.values.firstOrNull { it.name == name }
    }

    override suspend fun insertParty(party: Party) {
        partyMap[party.uid] = party
    }

    override suspend fun insertCategory(category: Category) {
        categoryMap[category.uid] = category
    }

    override suspend fun deleteCategory(category: Category) {
        categoryMap.remove(category.uid)
    }

    override suspend fun insertCategories(categories: List<Category>) {
        categoryMap.putAll(categories.map { it.uid to it })
    }

    override suspend fun deleteAllParties() {
        partyMap.clear()
    }

    override suspend fun deleteAllCategories() {
        categoryMap.clear()
    }
}
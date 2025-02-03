package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * A data class representing a group of [Member]s that make up a [Party]
 * @property partyName The name of this party
 * @property members The members of this party with their assignments
 * @property assigneeList The name of the [Category] used as assignees for [Member]s
 */
@Entity
data class Party(
    @PrimaryKey val uid : UUID,
    var partyName: String,
    var members: ArrayList<Member>,
    val assigneeList: String) {

    /**
     * A function to return all [Category] names used as assignments for this [Party]
     * @return list of [Category] names
     */
    fun getAllCategoryNames(): List<String> {
        val categories = arrayListOf<String>()
        members.get(0).assignments.forEach { categoryName, _ ->
          categories.add(categoryName)
        }
        return categories
    }
}
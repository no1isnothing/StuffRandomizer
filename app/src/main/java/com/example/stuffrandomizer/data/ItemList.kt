package com.example.stuffrandomizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * A data class representing a list that can be used to create a [Match].
 *
 * For example, a list of Aedra would be:
 * (listName: "Aedra", items: ["Talos", "Julianos", ..., "Arkay", "Akatosh"])
 */
@Entity
data class ItemList(
    @PrimaryKey val uid : UUID,
    val listName: String,
    var items: List<String>
)
package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * A data class representing a list that can be used to create a [Member].
 *
 * @property uid The unique identified for this [Category]
 * @property name The name of this [Category]
 * @property things The things that make up this [Category]
 *
 * For example, a [Category] representing Aedra would be:
 * (name: "Aedra", things: ["Talos", "Julianos", ..., "Arkay", "Akatosh"])
 */
@Entity
data class Category(
    @PrimaryKey val uid : UUID = UUID.randomUUID(),
    var name: String,
    var things: List<String>,
)
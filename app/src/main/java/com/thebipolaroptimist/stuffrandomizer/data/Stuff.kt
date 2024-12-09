package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * A data class representing a list that can be used to create a [Member].
 *
 * For example, a [Stuff] representing Aedra would be:
 * (name: "Aedra", things: ["Talos", "Julianos", ..., "Arkay", "Akatosh"])
 */
@Entity
data class Stuff(
    @PrimaryKey val uid : UUID,
    val name: String,
    var things: List<String>
)
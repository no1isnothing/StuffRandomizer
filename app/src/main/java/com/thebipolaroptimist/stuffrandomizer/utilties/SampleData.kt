package com.thebipolaroptimist.stuffrandomizer.utilties

import com.thebipolaroptimist.stuffrandomizer.data.Category
import com.thebipolaroptimist.stuffrandomizer.data.Member
import com.thebipolaroptimist.stuffrandomizer.data.Party
import java.util.UUID

object SampleData {

    /**
     * Build the default "BG Races"/"BG Backgrounds"/"BG Classes" [Category]s from the app's
     * string-array resources.
     */
    fun createDefaultCategories(
        races: List<String>,
        backgrounds: List<String>,
        classes: List<String>,
    ): List<Category> = listOf(
        Category(UUID.randomUUID(), "BG Races", races),
        Category(UUID.randomUUID(), "BG Backgrounds", backgrounds),
        Category(UUID.randomUUID(), "BG Classes", classes),
    )

    fun createSamplePartyData(): Party = Party(
        UUID.randomUUID(),
        "Skyrim 2024",
        arrayListOf(
            Member("Jane", hashMapOf(Pair("Aedra", "Talos"), Pair("Daedra", "Clavicus Vile"))),
            Member("Bear", hashMapOf(Pair("Aedra", "Mara"), Pair("Daedra", "Peryite"))),
            Member(
                "The Tooth Fairy",
                hashMapOf(Pair("Aedra", "Julianos"), Pair("Daedra", "Vaermina"))
            ),
            Member("Gifty", hashMapOf(Pair("Aedra", "Stendar"), Pair("Daedra", "Merida"))),
        ),
        "Friends"
    )

    fun createSampleCategoryData(): List<Category> = listOf(
        Category(
            UUID.randomUUID(),
            "TES Aedra",
            listOf(
                "Talos",
                "Julianos",
                "Arkay",
                "Akatosh",
                "Mara",
                "Stendarr",
                "Dibella",
                "Kynareth",
                "Zenithar"
            )
        ),
        Category(
            UUID.randomUUID(),
            "TES Daedra",
            listOf(
                "Clavicus Vile",
                "Meridia",
                "Peryite",
                "Azura",
                "Molag Bal",
                "Hermaus Mora",
                "Vaermina",
                "Nocturnal"
            )
        ),
        Category(
            UUID.randomUUID(),
            "Friends",
            listOf(
                "Jane",
                "Bear",
                "Gift",
                "The Tooth Fairy"
            )
        )
    )
}

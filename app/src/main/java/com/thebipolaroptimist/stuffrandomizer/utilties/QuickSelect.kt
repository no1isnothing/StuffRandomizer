package com.thebipolaroptimist.stuffrandomizer.utilties

object QuickSelect {

    /**
     * Pick a random non-blank entry from [items], or `null` if there are none.
     */
    fun pickRandom(items: List<String>): String? = items.filter { it.isNotEmpty() }.randomOrNull()
}

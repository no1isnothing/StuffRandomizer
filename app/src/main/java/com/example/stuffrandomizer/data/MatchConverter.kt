package com.example.stuffrandomizer.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * A typeConverter for types stored in [MatchDatabase].
 */
class MatchConverter {
    @TypeConverter
    fun matchListToString(matchSet: List<Match>): String =
        Gson().toJson(matchSet).toString()


    @TypeConverter
    fun stringToMatchList(matchSet: String): List<Match> =
        Gson().fromJson(matchSet, object : TypeToken<List<Match>>() {}.type)

    @TypeConverter
    fun itemListToString(itemList: List<String>): String =
        Gson().toJson(itemList).toString()

    @TypeConverter
    fun stringToItemList(itemList: String): List<String> =
        Gson().fromJson(itemList, object : TypeToken<List<String>>() {}.type)
}
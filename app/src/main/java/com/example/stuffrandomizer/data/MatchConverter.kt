package com.example.stuffrandomizer.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MatchConverter {
    @TypeConverter
    fun matchListToString(matchSet: List<Match>): String =
        Gson().toJson(matchSet).toString()


    @TypeConverter
    fun stringToMatchList(matchSet: String): List<Match> =
        Gson().fromJson(matchSet, object : TypeToken<List<Match>>() {}.type)
}
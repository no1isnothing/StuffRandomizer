package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * A typeConverter for types stored in [MainDatabase].
 */
class MainConverter {
    @TypeConverter
    fun memberListToString(members: List<Member>): String =
        Gson().toJson(members).toString()


    @TypeConverter
    fun stringToMemberList(pairings: String): List<Member> =
        Gson().fromJson(pairings, object : TypeToken<List<Member>>() {}.type)

    @TypeConverter
    fun listToString(list: List<String>): String =
        Gson().toJson(list).toString()

    @TypeConverter
    fun stringToList(s: String): List<String> =
        Gson().fromJson(s, object : TypeToken<List<String>>() {}.type)
}
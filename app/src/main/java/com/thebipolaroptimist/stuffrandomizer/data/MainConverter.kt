package com.thebipolaroptimist.stuffrandomizer.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * A typeConverter for types stored in [MainDatabase].
 */
class MainConverter {
    @TypeConverter
    fun pairingListToString(members: List<Member>): String =
        Gson().toJson(members).toString()


    @TypeConverter
    fun stringToPairingList(pairings: String): List<Member> =
        Gson().fromJson(pairings, object : TypeToken<List<Member>>() {}.type)

    @TypeConverter
    fun stuffListToString(stuff: List<String>): String =
        Gson().toJson(stuff).toString()

    @TypeConverter
    fun stringToStuffList(stuff: String): List<String> =
        Gson().fromJson(stuff, object : TypeToken<List<String>>() {}.type)
}
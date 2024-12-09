package com.thebipolaroptimist.stuffrandomizer.data

//package com.thebipolaroptimist

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.UUID

class StuffDaoTest {

    @Test
    fun insertTest_success() = runTest {
        val stuff = Stuff(UUID.randomUUID(), "List1", listOf("item1", "item2"))
        dao.insert(stuff)

        assertEquals(stuff, dao.getAllItemLists().first())
    }

    companion object {
        private lateinit var database: MainDatabase
        private lateinit var dao: StuffDao

        @JvmStatic
        @BeforeAll
        fun setup() {
            val context = ApplicationProvider.getApplicationContext<Context>()
            database = Room.inMemoryDatabaseBuilder(context, MainDatabase::class.java).build()
            dao = database.itemListDao()
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            database.close()
        }
    }

}
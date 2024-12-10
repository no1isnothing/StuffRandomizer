package com.thebipolaroptimist.stuffrandomizer.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.UUID

class CategoryDaoTest {

    @Test
    fun insertTest_success() = runTest {
        val category = Category(UUID.randomUUID(), "List1", listOf("item1", "item2"))
        dao.insert(category)
        assertEquals(category, dao.getAllCategories().first().first())
    }

    companion object {
        private lateinit var database: MainDatabase
        private lateinit var dao: CategoryDao

        @JvmStatic
        @BeforeAll
        fun setup() {
            val context = ApplicationProvider.getApplicationContext<Context>()
            database = Room.inMemoryDatabaseBuilder(context, MainDatabase::class.java).build()
            dao = database.categoryDao()
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            database.close()
        }
    }

}
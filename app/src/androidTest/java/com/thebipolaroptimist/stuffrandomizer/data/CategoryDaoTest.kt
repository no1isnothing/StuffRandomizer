package com.thebipolaroptimist.stuffrandomizer.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.UUID

class CategoryDaoTest {

    @Test
    fun insert_singleCategory_success() = runTest {
        val uuid = UUID.randomUUID()
        val category = Category(uuid, "List1", listOf("item1", "item2"))

        dao.insert(category)

        assertEquals(category, dao.getById(uuid))
    }

    @Test
    fun insert_updateCategory_success() = runTest {
        val uuid = UUID.randomUUID()
        val category = Category(uuid, "List2", listOf("item1", "item2"))
        dao.insert(category)

        val categoryUpdated = Category(uuid, "NewList", listOf("item3", "item4"))
        dao.insert(categoryUpdated)

        assertEquals(categoryUpdated, dao.getById(uuid))
    }

    @Test
    fun insert_multipleCategories_success() = runTest {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val category1 = Category(uuid1, "Category3",
            listOf("t1", "t2", "t2")
        )
        val category2 = Category(uuid2, "Category4",
            listOf("t1", "t2", "t2"))
        val categories = listOf(category1, category2)

        dao.insert(categories)

        assertEquals(categories, dao.getByIds(listOf( uuid1, uuid2)))
    }

    @Test
    fun getCategoryByName_categoryPresent_success() = runTest {
        val categoryName = "Cat1"
        val category1 = Category(UUID.randomUUID(), categoryName,
            listOf("t1", "t2", "t2")
        )
        val category2 = Category(UUID.randomUUID(), "Cat2",
            listOf("t1", "t2", "t2"))
        val categories = listOf(category1, category2)

        dao.insert(categories)

        assertEquals(category1, dao.getCategoryByName(categoryName))
    }

    @Test
    fun getCategoryByName_categoryNotPresent_returnsNull() = runTest {
        assertNull(dao.getCategoryByName("Cat"))
    }

    @Test
    fun getCategoriesByName_allPresent_success() = runTest  {
        val categoryName = "Cat11"
        val category1 = Category(UUID.randomUUID(), categoryName,
            listOf("t1", "t2", "t2")
        )
        val categoryName2 = "Cat12"
        val category2 = Category(UUID.randomUUID(), categoryName2,
            listOf("t1", "t2", "t2"))
        val categories = listOf(category1, category2)

        dao.insert(categories)

        assertEquals(categories, dao.getCategoriesByName(listOf(categoryName, categoryName2)))
    }

    @Test
    fun getCategoriesByName_nonePresent_empty() = runTest  {
        assertTrue(dao.getCategoriesByName(listOf("NotACategory", "Nope")).isEmpty())
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
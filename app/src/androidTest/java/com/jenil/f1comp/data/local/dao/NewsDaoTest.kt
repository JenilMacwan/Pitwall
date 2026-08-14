package com.jenil.f1comp.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jenil.f1comp.data.local.AppDatabase
import com.jenil.f1comp.data.local.entity.NewsEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: NewsDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.newsDao
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetNews() = runBlocking {
        val news = listOf(
            NewsEntity("link1", "Title 1", "Desc 1", "date1", "image1", "Source 1", 1000L),
            NewsEntity("link2", "Title 2", "Desc 2", "date2", "image2", "Source 2", 2000L)
        )
        dao.insertNews(news)

        val cachedNews = dao.getCachedNews().first()
        assertEquals(2, cachedNews.size)
        assertEquals("Title 2", cachedNews[0].title) // Ordered by cachedAt DESC
    }

    @Test
    fun deleteAllNews() = runBlocking {
        val news = listOf(
            NewsEntity("link1", "Title 1", "Desc 1", "date1", "image1", "Source 1", 1000L)
        )
        dao.insertNews(news)
        dao.deleteAllNews()

        val cachedNews = dao.getCachedNews().first()
        assertEquals(0, cachedNews.size)
    }
}

package com.jenil.f1comp.viewmodel

import app.cash.turbine.test
import com.jenil.f1comp.data.local.entity.NewsEntity
import com.jenil.f1comp.data.repository.NewsRepository
import com.jenil.f1comp.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NewsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: NewsRepository
    private lateinit var viewModel: NewsViewModel

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
    }

    @Test
    fun `init calls refreshNews and news state is updated`() = runTest {
        val mockNews = listOf(
            NewsEntity("link1", "Title 1", "Desc 1", "Fri, 14 Aug 2026 10:00:00 +0000", "image1", "Source 1", 0),
            NewsEntity("link2", "Title 2", "Desc 2", "Fri, 14 Aug 2026 11:00:00 +0000", "image2", "Source 2", 0)
        )
        every { repository.getCachedNews() } returns flowOf(mockNews)
        
        viewModel = NewsViewModel(repository)

        viewModel.news.test {
            val item = awaitItem()
            assertEquals(2, item.size)
            assertEquals("Title 2", item[0].title) // Check sorting by descending published date if applicable
        }
        
        coVerify { repository.refreshNews() }
    }
}

package com.jenil.f1comp.viewmodel

import app.cash.turbine.test
import com.jenil.f1comp.data.local.entity.DriverStandingsEntity
import com.jenil.f1comp.data.repository.DriverStandingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DriverStandingsViewModelTest {

    private lateinit var viewModel: DriverStandingsViewModel
    private val repository: DriverStandingsRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Default mocks
        every { repository.getCachedDriverStandings() } returns flowOf(emptyList())
        coEvery { repository.refreshDriverStandings() } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should call refreshDriverStandings and repository getCachedDriverStandings`() = runTest {
        // When
        viewModel = DriverStandingsViewModel(repository)
        advanceUntilIdle()

        // Then
        coVerify { repository.refreshDriverStandings() }
        coVerify { repository.getCachedDriverStandings() }
    }

    @Test
    fun `driverStandings should initially be empty and then update from repository`() = runTest {
        // Given
        val mockData = listOf(
            DriverStandingsEntity("max_verstappen", 1, 25.0, "Max Verstappen", "Dutch", "url", "Red Bull")
        )
        every { repository.getCachedDriverStandings() } returns flowOf(mockData)

        // When
        viewModel = DriverStandingsViewModel(repository)

        // Then
        viewModel.driverStandings.test {
            assertEquals(emptyList<DriverStandingsEntity>(), awaitItem())
            assertEquals(mockData, awaitItem())
        }
    }

    @Test
    fun `refreshDriverStandings should update isLoading state correctly`() = runTest {
        // Given
        coEvery { repository.refreshDriverStandings() } coAnswers {
            delay(1000)
        }

        // When
        viewModel = DriverStandingsViewModel(repository)
        
        viewModel.isLoading.test {
            // Initial state (before init's refresh starts executing its body)
            assertEquals(false, awaitItem())
            
            // Trigger init's refresh
            advanceTimeBy(1) 
            assertEquals(true, awaitItem())
            
            // Finish init's refresh
            advanceUntilIdle()
            assertEquals(false, awaitItem())
            
            // Manual refresh
            viewModel.refreshDriverStandings()
            assertEquals(true, awaitItem())
            advanceUntilIdle()
            assertEquals(false, awaitItem())
        }
    }

    @Test
    fun `refreshDriverStandings should set error message on failure`() = runTest {
        // Given
        val errorMessage = "Network Error"
        coEvery { repository.refreshDriverStandings() } throws Exception(errorMessage)

        // When
        viewModel = DriverStandingsViewModel(repository)
        advanceUntilIdle()

        // Then
        viewModel.error.test {
            assertEquals(errorMessage, awaitItem())
        }
    }

    @Test
    fun `refreshDriverStandings should clear error before starting new request`() = runTest {
        // Given - Start with an error
        coEvery { repository.refreshDriverStandings() } throws Exception("First Error")
        viewModel = DriverStandingsViewModel(repository)
        advanceUntilIdle()
        
        assertEquals("First Error", viewModel.error.value)

        // Mock a successful next call that takes time
        coEvery { repository.refreshDriverStandings() } coAnswers {
            delay(1000)
        }
        
        // When
        viewModel.refreshDriverStandings()
        
        // Then
        viewModel.error.test {
            // It should be the first error initially in the flow
            assertEquals("First Error", awaitItem())
            
            // Advance slightly to let the new refresh start
            advanceTimeBy(1)
            
            // Should be cleared to null
            assertNull(awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}

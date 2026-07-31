package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.DriverStandingsDao
import com.jenil.f1comp.data.local.entity.DriverStandingsEntity
import com.jenil.f1comp.data.model.DriverStandingsResponse
import com.jenil.f1comp.data.model.Standings
import com.jenil.f1comp.data.remote.F1ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DriverStandingsRepositoryTest {

    private lateinit var repository: DriverStandingsRepository
    private val apiService: F1ApiService = mockk()
    private val driverStandingsDao: DriverStandingsDao = mockk(relaxed = true)

    @Before
    fun setUp() {
        repository = DriverStandingsRepository(apiService, driverStandingsDao)
    }

    @Test
    fun `refreshDriverStandings should fetch from API and save to DAO`() = runTest {
        // Given
        val mockStandings = listOf(
            Standings("1", "25", "max_verstappen", "Max Verstappen", "Dutch", "url", "Red Bull")
        )
        val mockResponse = DriverStandingsResponse(mockStandings)
        coEvery { apiService.getDriverStandings() } returns mockResponse

        // When
        repository.refreshDriverStandings()

        // Then
        coVerify { apiService.getDriverStandings() }
        val expectedEntities = listOf(
            DriverStandingsEntity("max_verstappen", 1, 25.0, "Max Verstappen", "Dutch", "url", "Red Bull")
        )
        coVerify { driverStandingsDao.refreshDriverStandings(expectedEntities) }
    }
}

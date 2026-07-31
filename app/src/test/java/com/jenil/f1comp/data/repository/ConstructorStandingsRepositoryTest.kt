package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.ConstructorStandingsDao
import com.jenil.f1comp.data.local.entity.ConstructorStandingsEntity
import com.jenil.f1comp.data.model.ConstructorStandingsResponse
import com.jenil.f1comp.data.model.Standings
import com.jenil.f1comp.data.remote.F1ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ConstructorStandingsRepositoryTest {

    private lateinit var repository: ConstructorStandingsRepository
    private val apiService: F1ApiService = mockk()
    private val constructorStandingsDao: ConstructorStandingsDao = mockk(relaxed = true)

    @Before
    fun setUp() {
        repository = ConstructorStandingsRepository(apiService, constructorStandingsDao)
    }

    @Test
    fun `refreshConstructorStandings should fetch from API and save to DAO`() = runTest {
        // Given
        val mockStandings = listOf(
            Standings("1", "44", null, "Red Bull", "Austrian", null, null)
        )
        val mockResponse = ConstructorStandingsResponse(mockStandings)
        coEvery { apiService.getConstructorStandings() } returns mockResponse

        // When
        repository.refreshConstructorStandings()

        // Then
        coVerify { apiService.getConstructorStandings() }
        val expectedEntities = listOf(
            ConstructorStandingsEntity("Red Bull", 1, 44.0, "Austrian")
        )
        coVerify { constructorStandingsDao.refreshConstructorStandings(expectedEntities) }
    }
}

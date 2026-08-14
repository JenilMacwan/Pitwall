package com.jenil.f1comp.data.repository

import android.util.Log
import com.jenil.f1comp.data.local.dao.DriverProfileDao
import com.jenil.f1comp.data.model.Driver
import com.jenil.f1comp.data.model.DriverProfileResponse
import com.jenil.f1comp.data.remote.F1ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DriverProfileRepositoryTest {

    private lateinit var apiService: F1ApiService
    private lateinit var driverProfileDao: DriverProfileDao
    private lateinit var repository: DriverProfileRepository

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        
        apiService = mockk()
        driverProfileDao = mockk(relaxed = true)
        repository = DriverProfileRepository(apiService, driverProfileDao)
    }

    @Test
    fun `refreshDriverProfiles calls API and updates DAO`() = runTest {
        val mockResponse = DriverProfileResponse(
            season = "2026",
            totalDrivers = 1,
            drivers = listOf(
                Driver(
                    driverId = "lewis",
                    firstName = "Lewis",
                    lastName = "Hamilton",
                    fullName = "Lewis Hamilton",
                    number = "44",
                    code = "HAM",
                    nationality = "British",
                    image = "url",
                    team = "Ferrari"
                )
            )
        )
        coEvery { apiService.getDriverProfile() } returns mockResponse

        repository.refreshDriverProfiles()

        coVerify { apiService.getDriverProfile() }
        coVerify { driverProfileDao.refreshDriverProfiles(any()) }
    }
}

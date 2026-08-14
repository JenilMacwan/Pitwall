package com.jenil.f1comp.data.repository

import android.util.Log
import com.jenil.f1comp.data.local.dao.DriverProfileDao
import com.jenil.f1comp.data.local.entity.DriverProfileEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DriverProfileRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val driverProfileDao: DriverProfileDao
) {
    fun getCachedDriverProfiles(): Flow<List<DriverProfileEntity>> {
        return driverProfileDao.getCachedDriverProfiles()
    }

    suspend fun refreshDriverProfiles() {
        Log.d("DriverProfileRepo", "Refreshing drivers from API: https://f1companion-api.vercel.app/drivers_profile")
        val response = apiService.getDriverProfile()
        Log.d("DriverProfileRepo", "Fetched ${response.drivers.size} drivers")
        val profileEntities = response.drivers.map { networkModel ->
            DriverProfileEntity(
                driverId = networkModel.driverId,
                firstName = networkModel.firstName,
                lastName = networkModel.lastName,
                fullName = networkModel.fullName,
                number = networkModel.number,
                code = networkModel.code,
                nationality = networkModel.nationality,
                image = networkModel.image,
                team = networkModel.team,
                careerStats = networkModel.careerStats
            )
        }
        Log.d("DriverProfileRepo", "Mapping complete. Updating DB...")
        driverProfileDao.refreshDriverProfiles(profileEntities)
        Log.d("DriverProfileRepo", "DB update successful")
    }
}

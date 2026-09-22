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
        val response = apiService.getDriverProfile()
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
                headshotUrl = networkModel.headshotUrl,
                team = networkModel.team,
                born = networkModel.born,
                debut = networkModel.debut,
                about = networkModel.about,
                careerStats = networkModel.careerStats
            )
        }
        Log.d("DriverProfileRepo", "Mapping complete. Updating DB...")
        driverProfileDao.refreshDriverProfiles(profileEntities)
        Log.d("DriverProfileRepo", "DB update successful")
    }
}

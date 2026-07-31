package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.DriverDao
import com.jenil.f1comp.data.local.entity.DriverEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DriverInfoRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val driverDao: DriverDao
) {
    fun getCachedDriverInfo(): Flow<List<DriverEntity>> {
        return driverDao.getAllDrivers()
    }

    suspend fun refreshDriverInfo() {
        val response = apiService.getDriverProfile() // Updated endpoint method
        val driverEntities = response.drivers.map { networkModel ->
            DriverEntity(
                driverId = networkModel.driverId,
                firstName = networkModel.firstName,
                lastName = networkModel.lastName,
                fullName = networkModel.fullName,
                number = networkModel.number,
                code = networkModel.code,
                nationality = networkModel.nationality,
                image = networkModel.image,
                team = networkModel.team,
                worldChampionships = networkModel.careerStats?.worldChampionships ?: 0,
                totalWins = networkModel.careerStats?.totalWins ?: 0,
                totalPodiums = networkModel.careerStats?.totalPodiums ?: 0,
                currentSeasonPosition = networkModel.careerStats?.currentSeason?.position,
                currentSeasonPoints = networkModel.careerStats?.currentSeason?.points
            )
        }
        driverDao.refreshDrivers(driverEntities)
    }
}
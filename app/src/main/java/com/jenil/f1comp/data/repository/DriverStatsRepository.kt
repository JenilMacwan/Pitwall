package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.DriverStatsDao
import com.jenil.f1comp.data.local.entity.DriverStatsEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DriverStatsRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val driverStatsDao: DriverStatsDao
) {
    fun getCachedDriverStats(): Flow<List<DriverStatsEntity>> {
        return driverStatsDao.getCachedDriverStats()
    }

    suspend fun refreshDriverStats() {
        val response = apiService.getDriverStats()
        val driverStatsEntities = response.gridStats.map { networkModel ->
            DriverStatsEntity(
                driverId = networkModel.driverId,
                driverName = networkModel.driverName,
                careerStats = networkModel.careerStats
            )
        }
        driverStatsDao.refreshDriverStats(driverStatsEntities)
    }
}
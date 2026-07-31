package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.ConstructorStatsDao
import com.jenil.f1comp.data.local.entity.ConstructorsStatsEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConstructorStatsRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val constructorStatsDao: ConstructorStatsDao
) {
    fun getCachedStats(): Flow<List<ConstructorsStatsEntity>> {
        return constructorStatsDao.getCachedConstructorStats()
    }

    suspend fun refreshStats() {
        val response = apiService.getConstructorStats()
        val constructorStatsEntities = response.constructorStats.map { networkModel ->
            ConstructorsStatsEntity(
                constructorId = networkModel.constructorId,
                constructorName = networkModel.constructorName,
                stats = networkModel.stats,
            )
        }
        constructorStatsDao.refreshConstructorStats(constructorStatsEntities)
    }
}
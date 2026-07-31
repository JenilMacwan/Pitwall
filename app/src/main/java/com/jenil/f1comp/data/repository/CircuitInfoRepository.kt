package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.CircuitInfoDao
import com.jenil.f1comp.data.local.entity.CircuitInfoEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CircuitInfoRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val circuitInfoDao: CircuitInfoDao
) {
    fun getCachedCircuitInfo(): Flow<List<CircuitInfoEntity>> {
        return circuitInfoDao.getAllCircuits()
    }

    suspend fun refreshCircuitInfo() {
        val response = apiService.getCircuitInfo()
        val circuitEntities = response.circuits.map { networkModel ->
            CircuitInfoEntity(
                circuitId = networkModel.circuitId,
                circuitName = networkModel.circuitName,
                circuitLocation = networkModel.circuitLocation,
                circuitCountry = networkModel.circuitCountry,
                circuitLayout = networkModel.circuitLayout
            )
        }
        circuitInfoDao.refreshCircuits(circuitEntities)

    }
}
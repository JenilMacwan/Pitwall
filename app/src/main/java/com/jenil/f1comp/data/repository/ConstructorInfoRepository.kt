package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.ConstructorDao
import com.jenil.f1comp.data.local.entity.ConstructorEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConstructorInfoRepository @Inject constructor (
    private val apiService: F1ApiService,
    private val constructorDao: ConstructorDao
) {
    fun getCachedConstructorInfo(): Flow<List<ConstructorEntity>> {
        return constructorDao.getAllConstructors()
    }

    suspend fun refreshConstructorInfo() {

        val response = apiService.getConstructorsInfo()
        val constructorEntities = response.constructors.map { networkModel ->
            ConstructorEntity(
                constructorId = networkModel.constructorId,
                name = networkModel.name,
                nationality = networkModel.nationality,
                url = networkModel.url,
            )
        }
        constructorDao.refreshConstructors(constructorEntities)
    }
}
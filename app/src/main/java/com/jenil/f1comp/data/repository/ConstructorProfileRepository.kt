package com.jenil.f1comp.data.repository

import android.util.Log
import com.jenil.f1comp.data.local.dao.ConstructorProfileDao
import com.jenil.f1comp.data.local.entity.ConstructorProfileEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConstructorProfileRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val constructorProfileDao: ConstructorProfileDao
) {
    fun getCachedConstructorProfiles(): Flow<List<ConstructorProfileEntity>> {
        return constructorProfileDao.getCachedConstructorProfiles()
    }

    suspend fun refreshConstructorProfiles() {
        Log.d("ConstructorProfileRepo", "Refreshing constructors from API: https://f1companion-api.vercel.app/constructors_profile")
        val response = apiService.getConstructorProfile()
        Log.d("ConstructorProfileRepo", "Fetched ${response.constructors.size} constructors")
        val profileEntities = response.constructors.map { networkModel ->
            ConstructorProfileEntity(
                constructorId = networkModel.constructorId,
                name = networkModel.name,
                nationality = networkModel.nationality,
                logo = networkModel.logo,
                car = networkModel.car,
                drivers = networkModel.drivers,
                careerStats = networkModel.careerStats
            )
        }
        Log.d("ConstructorProfileRepo", "Mapping complete. Updating DB...")
        constructorProfileDao.refreshConstructorProfiles(profileEntities)
        Log.d("ConstructorProfileRepo", "DB update successful")
    }
}

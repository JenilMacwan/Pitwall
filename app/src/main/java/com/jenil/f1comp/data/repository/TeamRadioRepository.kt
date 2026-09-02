package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.TeamRadioDao
import com.jenil.f1comp.data.local.entity.TeamRadioEntity
import com.jenil.f1comp.data.local.entity.toEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TeamRadioRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val teamRadioDao: TeamRadioDao
) {

    fun getCachedTeamRadio(): Flow<List<TeamRadioEntity>> {
        return teamRadioDao.getAllTeamRadio()
    }

    fun getCachedTeamRadioByDriver(driverCode: String): Flow<List<TeamRadioEntity>> {
        return teamRadioDao.getTeamRadioByDriver(driverCode)
    }

    suspend fun refreshTeamRadio() {
        val response = apiService.getLatestTeamRadio()
        val entities = response.messages.map { message ->
            message.toEntity(
                sessionName = response.sessionName,
                eventName = response.eventName
            )
        }
        teamRadioDao.refreshTeamRadios(entities)
    }
}

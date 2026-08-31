package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.TeammateH2HDao
import com.jenil.f1comp.data.local.entity.ComparisonSet
import com.jenil.f1comp.data.local.entity.DriverInfo
import com.jenil.f1comp.data.local.entity.RoundResult
import com.jenil.f1comp.data.local.entity.StandingsComparison
import com.jenil.f1comp.data.local.entity.TeammateHeadtoHeadEntity
import com.jenil.f1comp.data.model.SessionComparison
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TeammateHeadtoHeadRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val teammateH2HDao: TeammateH2HDao
) {
    fun getCachedHeadtoHead(): Flow<List<TeammateHeadtoHeadEntity>> {
        return teammateH2HDao.getHeadToHead()
    }

    suspend fun refreshHeadtoHead() {
        val response = apiService.getTeammateHead2Head()
        val season = response.season
        
        val h2hEntities = response.headToHead.map { networkModel ->
            TeammateHeadtoHeadEntity(
                constructorId = networkModel.constructorId,
                season = season,
                constructorName = networkModel.constructor,
                constructorLogo = networkModel.constructorLogo,
                driverA = DriverInfo(
                    driverId = networkModel.drivers.driverA.driverId,
                    name = networkModel.drivers.driverA.name,
                    code = networkModel.drivers.driverA.code,
                    image = networkModel.drivers.driverA.image
                ),
                driverB = DriverInfo(
                    driverId = networkModel.drivers.driverB.driverId,
                    name = networkModel.drivers.driverB.name,
                    code = networkModel.drivers.driverB.code,
                    image = networkModel.drivers.driverB.image
                ),
                qualifying = mapSession(networkModel.qualifying),
                sprintQualifying = networkModel.sprintQualifying?.let { mapSession(it) },
                sprintRace = networkModel.sprintRace?.let { mapSession(it) },
                race = mapSession(networkModel.race),
                standings = StandingsComparison(
                    driverAPosition = networkModel.standings.driverA.position,
                    driverAPoints = networkModel.standings.driverA.points,
                    driverBPosition = networkModel.standings.driverB.position,
                    driverBPoints = networkModel.standings.driverB.points,
                    pointsDelta = networkModel.standings.pointsDelta,
                    positionsDelta = networkModel.standings.positionsDelta
                ),
                cachedAt = System.currentTimeMillis()
            )
        }
        
        teammateH2HDao.clearHeadToHead()
        teammateH2HDao.insertHeadToHead(h2hEntities)
    }

    private fun mapSession(session: SessionComparison): ComparisonSet {
        return ComparisonSet(
            driverAWins = session.driverAWins,
            driverBWins = session.driverBWins,
            ties = session.ties,
            totalCompleted = session.totalCompleted,
            rounds = session.rounds.map { round ->
                RoundResult(
                    round = round.round,
                    raceName = round.raceName,
                    driverAPosition = round.driverAPosition,
                    driverBPosition = round.driverBPosition,
                    winner = round.winner
                )
            }
        )
    }
}

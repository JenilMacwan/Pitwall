package com.jenil.f1comp.data.remote

import com.jenil.f1comp.data.model.CircuitResponse
import com.jenil.f1comp.data.model.ConstructorResponse
import com.jenil.f1comp.data.model.ConstructorStandingsResponse
import com.jenil.f1comp.data.model.ConstructorStatsResponse
import com.jenil.f1comp.data.model.DriverProfileResponse
import com.jenil.f1comp.data.model.DriverStandingsResponse
import com.jenil.f1comp.data.model.DriverStatsResponse
import com.jenil.f1comp.data.model.NewsResponse
import com.jenil.f1comp.data.model.NextRace
import com.jenil.f1comp.data.model.RaceResultResponse
import com.jenil.f1comp.data.model.ScheduleResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface F1ApiService {
    @GET("drivers")
    suspend fun getDriverInfo(): DriverProfileResponse

    @GET("constructors")
    suspend fun getConstructorsInfo(): ConstructorResponse

    @GET("drivers_profile")
    suspend fun getDriverProfile(): DriverProfileResponse

    @GET("constructors_profile")
    suspend fun getConstructorProfile(): ConstructorResponse


    @GET("schedule")
    suspend fun getSchedule(): ScheduleResponse

    @GET("circuits")
    suspend fun getCircuitInfo(): CircuitResponse

    @GET("news")
    suspend fun getF1News(): NewsResponse

    @GET("next_race")
    suspend fun getNextRace(): NextRace

    @GET("driver_standings")
    suspend fun getDriverStandings(): DriverStandingsResponse

    @GET("constructor_standings")
    suspend fun getConstructorStandings(): ConstructorStandingsResponse

    @GET("race_results/{race_id}/{year}")
    suspend fun getRaceResult(
        @Path("race_id") raceId: String,
        @Path("year") year: Int
    ): RaceResultResponse

    @GET("drivers_stats")
    suspend fun getDriverStats(): DriverStatsResponse

    @GET("constructor_stats")
    suspend fun getConstructorStats(): ConstructorStatsResponse
}
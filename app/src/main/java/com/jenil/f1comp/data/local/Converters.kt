package com.jenil.f1comp.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jenil.f1comp.data.local.entity.ComparisonSet
import com.jenil.f1comp.data.local.entity.DriverInfo
import com.jenil.f1comp.data.local.entity.StandingsComparison
import com.jenil.f1comp.data.model.DriverCareerStats
import com.jenil.f1comp.data.model.RaceCountdown
import com.jenil.f1comp.data.model.RaceSession
import com.jenil.f1comp.data.model.RaceWeather
import com.jenil.f1comp.data.model.TeamCareerStats

class Converters{
    private val gson = Gson()

    @TypeConverter
    fun fromMyRaceSession(value: RaceSession?):String?{
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMyRaceSession(value: String?): RaceSession?{
        return gson.fromJson(value, RaceSession::class.java)
    }

    @TypeConverter
    fun fromMyRaceCountdown(value:RaceCountdown): String{
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMyRaceCountdown(value: String): RaceCountdown{
        return gson.fromJson(value, RaceCountdown::class.java)
    }

    @TypeConverter
    fun fromMyRaceWeather(value:RaceWeather?): String?{
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMyRaceWeather(value: String?): RaceWeather?{
        return gson.fromJson(value, RaceWeather::class.java)
    }

    @TypeConverter
    fun fromDriverCareerStats(value: DriverCareerStats?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDriverCareerStats(value: String?): DriverCareerStats? {
        return gson.fromJson(value, DriverCareerStats::class.java)
    }

    @TypeConverter
    fun fromTeamCareerStats(value: TeamCareerStats?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTeamCareerStats(value: String?): TeamCareerStats? {
        return gson.fromJson(value, TeamCareerStats::class.java)
    }

    @TypeConverter
    fun fromDriverInfo(value: DriverInfo?): String? = gson.toJson(value)

    @TypeConverter
    fun toDriverInfo(value: String?): DriverInfo? = gson.fromJson(value, DriverInfo::class.java)

    @TypeConverter
    fun fromComparisonSet(value: ComparisonSet?): String? = gson.toJson(value)

    @TypeConverter
    fun toComparisonSet(value: String?): ComparisonSet? = gson.fromJson(value, ComparisonSet::class.java)

    @TypeConverter
    fun fromStandingsComparison(value: StandingsComparison?): String? = gson.toJson(value)

    @TypeConverter
    fun toStandingsComparison(value: String?): StandingsComparison? = gson.fromJson(value, StandingsComparison::class.java)

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
}
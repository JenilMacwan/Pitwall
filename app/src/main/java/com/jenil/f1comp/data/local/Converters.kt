package com.jenil.f1comp.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    fun fromStringList(value: List<String>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
}
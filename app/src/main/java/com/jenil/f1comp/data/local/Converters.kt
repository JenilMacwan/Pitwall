package com.jenil.f1comp.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.jenil.f1comp.data.model.CurrentStats
import com.jenil.f1comp.data.model.CurrentTeamStats
import com.jenil.f1comp.data.model.RaceCountdown
import com.jenil.f1comp.data.model.RaceResult
import com.jenil.f1comp.data.model.RaceSession
import com.jenil.f1comp.data.model.RaceWeather
import com.jenil.f1comp.data.model.Stats
import com.jenil.f1comp.data.model.TeamStats

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
    fun fromMyStats(value:Stats?): String?{
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMyStats(value: String?): Stats?{
        return gson.fromJson(value, Stats::class.java)
    }

    @TypeConverter
    fun fromMyCurrentStats(value: CurrentStats?): String?{
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMyCurrentStats(value: String?): CurrentStats?{
        return gson.fromJson(value, CurrentStats::class.java)
    }

    @TypeConverter
    fun fromMyTeamStats(value: TeamStats?): String?{
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMyTeamStats(value: String?): TeamStats?{
        return gson.fromJson(value, TeamStats::class.java)
    }

    @TypeConverter
    fun fromMyCurrentTeamStats(value: CurrentTeamStats?): String?{
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMyCurrentTeamStats(value: String?): CurrentTeamStats?{
        return gson.fromJson(value, CurrentTeamStats::class.java)
    }
}
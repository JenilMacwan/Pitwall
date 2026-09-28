package com.jenil.f1comp.data.model

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class LiveTelemetryResponse(
    @SerializedName("SessionInfo")
    val sessionInfo: SessionInfo? = null,
    @SerializedName("DriverList")
    val rawDriverList: JsonObject? = null,
    @SerializedName("WeatherData")
    val weatherData: WeatherData? = null,
    @SerializedName("TrackStatus")
    val trackStatus: TrackStatus? = null,
    @SerializedName("TimingData")
    val timingData: TimingDataContainer? = null,
    @SerializedName("TimingAppData")
    val timingAppData: TimingAppDataContainer? = null,
    @SerializedName("RaceControlMessages")
    val raceControlMessages: List<RaceControlMessage> = emptyList(),
    @SerializedName("ExtrapolatedClock")
    val extrapolatedClock: ExtrapolatedClock? = null,
) {
    val driverList: Map<String, DriverInfo>?
        get() {
            val gson = Gson()
            return rawDriverList?.entrySet()?.mapNotNull { entry ->
                // Skip the internal "_kf" key or any non-objects
                if (entry.key == "_kf" || !entry.value.isJsonObject) return@mapNotNull null

                try {
                    entry.key to gson.fromJson(entry.value, DriverInfo::class.java)
                } catch (e: Exception) {
                    null
                }
            }?.toMap()
        }
}

data class SessionInfo(
    @SerializedName("Type")
    val type: String? = null,
    @SerializedName("Name")
    val name: String? = null,
    @SerializedName("Meeting")
    val meeting: MeetingInfo? = null
)

data class MeetingInfo(
    @SerializedName("Name")
    val name: String? = null,
    @SerializedName("OfficialName")
    val officialName: String? = null,
    @SerializedName("Location")
    val location: String? = null
)

data class DriverInfo(
    @SerializedName("BroadcastName")
    val broadcastName: String? = null,
    @SerializedName("LastName")
    val lastName: String? = null,
    @SerializedName("TeamColour")
    val teamColour: String? = null
)

data class WeatherData(
    @SerializedName("AirTemp")
    val airTemp: String? = null,
    @SerializedName("TrackTemp")
    val trackTemp: String? = null,
    @SerializedName("Humidity")
    val humidity: String? = null,
    @SerializedName("Pressure")
    val pressure: String? = null,
    @SerializedName("Rainfall")
    val rainfall: String? = null,
    @SerializedName("WindDirection")
    val windDirection: String? = null,
    @SerializedName("WindSpeed")
    val windSpeed: String? = null
)

data class TrackStatus(
    @SerializedName("Status")
    val status: String? = null,
    @SerializedName("Message")
    val message: String? = null // e.g. "AllClear"
)

data class ExtrapolatedClock(
    @SerializedName("Utc")
    val utc: String? = null,

    @SerializedName("Remaining")
    val remaining: String? = null,

    @SerializedName("Extrapolating")
    val extrapolating: Boolean? = null
)

data class TimingDataContainer(
    @SerializedName("Lines")
    val lines: Map<String, TimingLine>? = null
)

data class TimingLine(
    @SerializedName("Position")
    val position: String? = null,
    @SerializedName("ShowPosition")
    val showPosition: Boolean? = null,
    @SerializedName("RacingNumber")
    val racingNumber: String? = null,
    @SerializedName("Retired")
    val retired: Boolean? = null,
    @SerializedName("Stopped")
    val stopped: Boolean? = null,
    @SerializedName("InPit")
    val inPit: Boolean? = null,
    @SerializedName("PitOut")
    val pitOut: Boolean? = null,
    @SerializedName("NumberOfLaps")
    val numberOfLaps: Int? = null,

    @SerializedName("TimeDiffToFastest")
    val timeDiffToFastest: String? = null,
    @SerializedName("TimeDiffToPositionAhead")
    val timeDiffToPositionAhead: String? = null,

    @SerializedName("GapToLeader")
    val gapToLeader: Any? = null,
    @SerializedName("IntervalToPositionAhead")
    val intervalToPositionAhead: Any? = null,

    @SerializedName("LastLapTime")
    val lastLapTime: LapTime? = null,

    @SerializedName("Sectors")
    val rawSectors: Any? = null
) {
    val sectors: List<SectorTime>?
        get() {
            val raw = rawSectors ?: return null
            return try {
                val gson = Gson()
                when (raw) {
                    is List<*> -> {
                        raw.mapNotNull { item ->
                            if (item == null) null
                            else gson.fromJson(gson.toJson(item), SectorTime::class.java)
                        }
                    }
                    is Map<*, *> -> {
                        raw.entries.sortedBy { it.key.toString() }.mapNotNull { entry ->
                            if (entry.value == null) null
                            else gson.fromJson(gson.toJson(entry.value), SectorTime::class.java)
                        }
                    }
                    else -> null
                }
            } catch (e: Exception) {
                null
            }
        }
}

data class LapTime(
    @SerializedName("Value")
    val value: String? = null,
    @SerializedName("OverallFastest")
    val isOverallFastest: Any? = null,
    @SerializedName("PersonalFastest")
    val isPersonalFastest: Any? = null
) {
    val overallFastest: Boolean
        get() = when (isOverallFastest) {
            is Boolean -> isOverallFastest
            is String -> isOverallFastest.equals("true", ignoreCase = true) || isOverallFastest == "1"
            is Number -> isOverallFastest.toInt() == 1
            else -> false
        }

    val personalFastest: Boolean
        get() = when (isPersonalFastest) {
            is Boolean -> isPersonalFastest
            is String -> isPersonalFastest.equals("true", ignoreCase = true) || isPersonalFastest == "1"
            is Number -> isPersonalFastest.toInt() == 1
            else -> false
        }
}

data class SectorTime(
    @SerializedName("Value")
    val value: String? = null,
    @SerializedName("OverallFastest")
    val isOverallFastest: Any? = null,
    @SerializedName("PersonalFastest")
    val isPersonalFastest: Any? = null
) {
    val overallFastest: Boolean
        get() = when (isOverallFastest) {
            is Boolean -> isOverallFastest
            is String -> isOverallFastest.equals("true", ignoreCase = true) || isOverallFastest == "1"
            is Number -> isOverallFastest.toInt() == 1
            else -> false
        }

    val personalFastest: Boolean
        get() = when (isPersonalFastest) {
            is Boolean -> isPersonalFastest
            is String -> isPersonalFastest.equals("true", ignoreCase = true) || isPersonalFastest == "1"
            is Number -> isPersonalFastest.toInt() == 1
            else -> false
        }
}

data class TimingAppDataContainer(
    @SerializedName("Lines")
    val lines: Map<String, AppDataLine>? = null
)

data class AppDataLine(
    @SerializedName("Line")
    val line: Int? = null,
    @SerializedName("Stints")
    val rawStints: Any? = null
) {
    val stints: List<Stint>?
        get() {
            val raw = rawStints ?: return null
            return try {
                val gson = Gson()
                when (raw) {
                    is List<*> -> {
                        raw.mapNotNull { item ->
                            if (item == null) null
                            else gson.fromJson(gson.toJson(item), Stint::class.java)
                        }
                    }
                    is Map<*, *> -> {
                        raw.entries.sortedBy { it.key.toString() }.mapNotNull { entry ->
                            if (entry.value == null) null
                            else gson.fromJson(gson.toJson(entry.value), Stint::class.java)
                        }
                    }
                    else -> null
                }
            } catch (e: Exception) {
                null
            }
        }
}

data class Stint(
    @SerializedName(value = "Compound", alternate = ["compound"])
    val compound: String? = null,
    @SerializedName("New")
    val new: String? = null,
    @SerializedName("TyresNotChanged")
    val tyresNotChanged: String? = null,
    @SerializedName("TotalLaps")
    val totalLaps: Int? = null,
    @SerializedName("StartLaps")
    val startLaps: Int? = null,
    @SerializedName("LapTime")
    val lapTime: String? = null,
    @SerializedName("LapNumber")
    val lapNumber: Int? = null,
    @SerializedName("NewLaps")
    val newLaps: Int? = null
)

data class RaceControlMessage(
    @SerializedName("Utc")
    val utc: String? = null,
    @SerializedName("Category")
    val category: String? = null, // e.g. "Flag", "SafetyCar"
    @SerializedName("Flag")
    val flag: String? = null,
    @SerializedName("Scope")
    val scope: String? = null,
    @SerializedName("Sector")
    val sector: Int? = null,
    @SerializedName("Message")
    val message: String? = null
)

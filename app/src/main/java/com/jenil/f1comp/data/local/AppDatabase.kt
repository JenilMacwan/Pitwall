package com.jenil.f1comp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jenil.f1comp.data.local.dao.CircuitInfoDao
import com.jenil.f1comp.data.local.dao.ConstructorProfileDao
import com.jenil.f1comp.data.local.dao.ConstructorStandingsDao
import com.jenil.f1comp.data.local.dao.DriverProfileDao
import com.jenil.f1comp.data.local.dao.DriverStandingsDao
import com.jenil.f1comp.data.local.dao.NewsDao
import com.jenil.f1comp.data.local.dao.NextRaceDao
import com.jenil.f1comp.data.local.dao.RaceResultDao
import com.jenil.f1comp.data.local.dao.ScheduleDao
import com.jenil.f1comp.data.local.entity.CircuitInfoEntity
import com.jenil.f1comp.data.local.entity.ConstructorProfileEntity
import com.jenil.f1comp.data.local.entity.ConstructorStandingsEntity
import com.jenil.f1comp.data.local.entity.DriverProfileEntity
import com.jenil.f1comp.data.local.entity.DriverStandingsEntity
import com.jenil.f1comp.data.local.entity.NewsEntity
import com.jenil.f1comp.data.local.entity.NextRaceEntity
import com.jenil.f1comp.data.local.entity.RaceResultEntity
import com.jenil.f1comp.data.local.entity.ScheduleEntity

@Database(
    entities = [
        ScheduleEntity::class,
        ConstructorProfileEntity::class,
        ConstructorStandingsEntity::class,
        DriverProfileEntity::class,
        DriverStandingsEntity::class,
        CircuitInfoEntity::class,
        RaceResultEntity::class,
        NextRaceEntity::class,
        NewsEntity::class
    ],
    version = 25,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val driverProfileDao: DriverProfileDao
    abstract val scheduleDao: ScheduleDao
    abstract val constructorProfileDao: ConstructorProfileDao
    abstract val circuitInfoDao: CircuitInfoDao
    abstract val raceResultDao: RaceResultDao
    abstract val nextRaceDao: NextRaceDao
    abstract val driverStandingsDao: DriverStandingsDao
    abstract val constructorStandingsDao: ConstructorStandingsDao
    abstract val newsDao: NewsDao
}

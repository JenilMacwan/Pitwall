package com.jenil.f1comp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jenil.f1comp.data.local.dao.CircuitInfoDao
import com.jenil.f1comp.data.local.dao.ConstructorDao
import com.jenil.f1comp.data.local.dao.ConstructorStandingsDao
import com.jenil.f1comp.data.local.dao.ConstructorStatsDao
import com.jenil.f1comp.data.local.dao.DriverDao
import com.jenil.f1comp.data.local.dao.DriverStandingsDao
import com.jenil.f1comp.data.local.dao.DriverStatsDao
import com.jenil.f1comp.data.local.dao.NewsDao
import com.jenil.f1comp.data.local.dao.NextRaceDao
import com.jenil.f1comp.data.local.dao.RaceResultDao
import com.jenil.f1comp.data.local.dao.ScheduleDao
import com.jenil.f1comp.data.local.entity.CircuitInfoEntity
import com.jenil.f1comp.data.local.entity.ConstructorEntity
import com.jenil.f1comp.data.local.entity.ConstructorStandingsEntity
import com.jenil.f1comp.data.local.entity.DriverEntity
import com.jenil.f1comp.data.local.entity.DriverStandingsEntity
import com.jenil.f1comp.data.local.entity.DriverStatsEntity
import com.jenil.f1comp.data.local.entity.NewsEntity
import com.jenil.f1comp.data.local.entity.NextRaceEntity
import com.jenil.f1comp.data.local.entity.RaceResultEntity
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import com.jenil.f1comp.data.local.entity.ConstructorsStatsEntity

@Database(
    entities = [
        ScheduleEntity::class,
        ConstructorEntity::class,
        ConstructorStandingsEntity::class,
        DriverEntity::class,
        DriverStandingsEntity::class,
        CircuitInfoEntity::class,
        RaceResultEntity::class,
        NextRaceEntity::class,
        NewsEntity::class,
        DriverStatsEntity::class,
        ConstructorsStatsEntity::class

    ],
    version = 20,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val driverDao: DriverDao
    abstract val scheduleDao: ScheduleDao
    abstract val constructorDao: ConstructorDao
    abstract val circuitInfoDao: CircuitInfoDao
    abstract val raceResultDao: RaceResultDao
    abstract val nextRaceDao: NextRaceDao
    abstract val driverStandingsDao: DriverStandingsDao
    abstract val constructorStandingsDao: ConstructorStandingsDao
    abstract val newsDao: NewsDao
    abstract val driverStatsDao: DriverStatsDao
    abstract val constructorStatsDao: ConstructorStatsDao
}
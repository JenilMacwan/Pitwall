package com.jenil.f1comp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jenil.f1comp.data.local.dao.ChatMessageDao
import com.jenil.f1comp.data.local.dao.CircuitInfoDao
import com.jenil.f1comp.data.local.dao.ConstructorProfileDao
import com.jenil.f1comp.data.local.dao.ConstructorStandingsDao
import com.jenil.f1comp.data.local.dao.DriverProfileDao
import com.jenil.f1comp.data.local.dao.DriverStandingsDao
import com.jenil.f1comp.data.local.dao.NewsDao
import com.jenil.f1comp.data.local.dao.NextRaceDao
import com.jenil.f1comp.data.local.dao.QualifyingResultDao
import com.jenil.f1comp.data.local.dao.RaceResultDao
import com.jenil.f1comp.data.local.dao.ScheduleDao
import com.jenil.f1comp.data.local.dao.SprintQualiDao
import com.jenil.f1comp.data.local.dao.SprintResultDao
import com.jenil.f1comp.data.local.dao.TeamRadioDao
import com.jenil.f1comp.data.local.dao.TeammateH2HDao
import com.jenil.f1comp.data.local.entity.ChatMessageEntity
import com.jenil.f1comp.data.local.entity.CircuitInfoEntity
import com.jenil.f1comp.data.local.entity.ConstructorProfileEntity
import com.jenil.f1comp.data.local.entity.ConstructorStandingsEntity
import com.jenil.f1comp.data.local.entity.DriverProfileEntity
import com.jenil.f1comp.data.local.entity.DriverStandingsEntity
import com.jenil.f1comp.data.local.entity.NewsEntity
import com.jenil.f1comp.data.local.entity.NextRaceEntity
import com.jenil.f1comp.data.local.entity.QualifyingResultEntity
import com.jenil.f1comp.data.local.entity.RaceResultEntity
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import com.jenil.f1comp.data.local.entity.SprintQualifyingResultEntity
import com.jenil.f1comp.data.local.entity.SprintResultEntity
import com.jenil.f1comp.data.local.entity.TeamRadioEntity
import com.jenil.f1comp.data.local.entity.TeammateHeadtoHeadEntity

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
        NewsEntity::class,
        TeammateHeadtoHeadEntity::class,
        TeamRadioEntity::class,
        QualifyingResultEntity::class,
        SprintQualifyingResultEntity::class,
        SprintResultEntity::class,
        ChatMessageEntity::class
    ],
    version = 53,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val chatMessageDao: ChatMessageDao
    abstract val driverProfileDao: DriverProfileDao
    abstract val scheduleDao: ScheduleDao
    abstract val constructorProfileDao: ConstructorProfileDao
    abstract val circuitInfoDao: CircuitInfoDao
    abstract val raceResultDao: RaceResultDao
    abstract val nextRaceDao: NextRaceDao
    abstract val driverStandingsDao: DriverStandingsDao
    abstract val constructorStandingsDao: ConstructorStandingsDao
    abstract val newsDao: NewsDao
    abstract val teammateH2HDao: TeammateH2HDao
    abstract val teamRadioDao: TeamRadioDao
    abstract val qualifyingResultDao: QualifyingResultDao
    abstract val sprintQualiDao: SprintQualiDao
    abstract val sprintResultDao: SprintResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_51_52 = object : Migration(51, 52) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `chat_messages` (`id` TEXT NOT NULL, `sessionId` TEXT NOT NULL, `text` TEXT NOT NULL, `isUser` INTEGER NOT NULL, `timestamp` TEXT NOT NULL, `referenceDocuments` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_chat_messages_sessionId_createdAt` ON `chat_messages` (`sessionId`, `createdAt`)")
            }
        }

        private val MIGRATION_52_53 = object : Migration(52, 53) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `chat_messages` ADD COLUMN `dataSources` TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "f1_comp_db"
                ).addMigrations(MIGRATION_51_52, MIGRATION_52_53)
                    .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

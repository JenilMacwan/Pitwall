package com.jenil.f1comp.di

import android.content.Context
import androidx.room.Room
import com.jenil.f1comp.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "f1_comp_db"
            ).fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideScheduleDao(db: AppDatabase) = db.scheduleDao

    @Provides
    @Singleton
    fun provideConstructorStandingsDao(db: AppDatabase) = db.constructorStandingsDao

    @Provides
    @Singleton
    fun provideDriverStandingDao(db: AppDatabase) = db.driverStandingsDao

    @Provides
    @Singleton
    fun provideNextRaceDao(db: AppDatabase) = db.nextRaceDao

    @Provides
    @Singleton
    fun provideCircuitInfoDao(db: AppDatabase) = db.circuitInfoDao

    @Provides
    @Singleton
    fun provideResultDao(db: AppDatabase) = db.raceResultDao

    @Provides
    @Singleton
    fun provideNewsDao(db: AppDatabase) = db.newsDao

    @Provides
    @Singleton
    fun provideDriverProfileDao(db: AppDatabase) = db.driverProfileDao

    @Provides
    @Singleton
    fun provideConstructorProfileDao(db: AppDatabase) = db.constructorProfileDao

    @Provides
    @Singleton
    fun provideTeammateH2HDao(db: AppDatabase) = db.teammateH2HDao
}

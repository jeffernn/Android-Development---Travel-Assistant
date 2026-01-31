package com.example.traveljournal.di

import com.example.traveljournal.data.local.database.TravelDatabase
import com.example.traveljournal.data.local.database.dao.TripDao
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
    fun provideTravelDatabase(@ApplicationContext context: android.content.Context): TravelDatabase {
        return TravelDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideTripDao(database: TravelDatabase): TripDao {
        return database.tripDao()
    }
}
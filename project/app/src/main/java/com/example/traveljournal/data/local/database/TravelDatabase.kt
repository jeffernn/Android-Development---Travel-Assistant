package com.example.traveljournal.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.traveljournal.data.local.database.dao.TripDao
import com.example.traveljournal.data.local.database.entity.LocationEntity
import com.example.traveljournal.data.local.database.entity.TripEntity
import com.example.traveljournal.util.Converters

@Database(
    entities = [TripEntity::class, LocationEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TravelDatabase : RoomDatabase() {
    
    abstract fun tripDao(): TripDao
    
    companion object {
        @Volatile
        private var INSTANCE: TravelDatabase? = null
        
        fun getDatabase(context: Context): TravelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TravelDatabase::class.java,
                    "travel_database"
                )
                .fallbackToDestructiveMigration() // 开发阶段使用，生产环境应使用迁移策略
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
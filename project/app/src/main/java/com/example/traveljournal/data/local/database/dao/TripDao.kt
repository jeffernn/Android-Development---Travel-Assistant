package com.example.traveljournal.data.local.database.dao

import androidx.room.*
import com.example.traveljournal.data.local.database.entity.LocationEntity
import com.example.traveljournal.data.local.database.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    
    // Trip相关操作
    @Query("SELECT * FROM trips ORDER BY created_at DESC")
    fun getAllTrips(): Flow<List<TripEntity>>
    
    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getTripById(id: Long): TripEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity): Long
    
    @Update
    suspend fun updateTrip(trip: TripEntity)
    
    @Delete
    suspend fun deleteTrip(trip: TripEntity)
    
    @Query("DELETE FROM trips WHERE id = :id")
    suspend fun deleteTripById(id: Long)
    
    // Location相关操作
    @Query("SELECT * FROM locations WHERE trip_id = :tripId ORDER BY created_at ASC")
    suspend fun getLocationsByTripId(tripId: Long): List<LocationEntity>
    
    @Query("SELECT * FROM locations WHERE trip_id = :tripId ORDER BY created_at ASC")
    fun getLocationsByTripIdFlow(tripId: Long): Flow<List<LocationEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity): Long
    
    @Update
    suspend fun updateLocation(location: LocationEntity)
    
    @Delete
    suspend fun deleteLocation(location: LocationEntity)
    
    @Query("DELETE FROM locations WHERE id = :id")
    suspend fun deleteLocationById(id: Long)
    
    // 复合查询
    @Transaction
    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getTripWithLocations(id: Long): TripWithLocations?
    
    @Query("SELECT * FROM trips WHERE title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%' ORDER BY created_at DESC")
    fun searchTrips(searchQuery: String): Flow<List<TripEntity>>
}

// 用于获取带有关联位置的旅行数据
data class TripWithLocations(
    @Embedded val trip: TripEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "trip_id"
    )
    val locations: List<LocationEntity> = emptyList()
)
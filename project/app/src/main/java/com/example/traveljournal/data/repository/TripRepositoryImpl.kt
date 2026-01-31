package com.example.traveljournal.data.repository

import com.example.traveljournal.data.local.database.dao.TripDao
import com.example.traveljournal.data.local.database.dao.TripWithLocations
import com.example.traveljournal.data.local.database.entity.LocationEntity
import com.example.traveljournal.data.local.database.entity.TripEntity
import com.example.traveljournal.domain.model.Location
import com.example.traveljournal.domain.model.Trip
import com.example.traveljournal.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TripRepositoryImpl(
    private val tripDao: TripDao
) : TripRepository {
    
    override fun getAllTrips(): Flow<List<Trip>> {
        return tripDao.getAllTrips().map { tripEntities ->
            tripEntities.map { it.toDomainModel() }
        }
    }
    
    override fun getTripById(id: Long): Flow<Trip?> {
        return kotlinx.coroutines.flow.flow {
            val tripWithLocations = tripDao.getTripWithLocations(id)
            emit(tripWithLocations?.toDomainModel())
        }
    }
    
    override suspend fun insertTrip(trip: Trip): Long {
        val tripEntity = trip.toEntity()
        val tripId = tripDao.insertTrip(tripEntity)

        // 如果旅行ID为0（新创建的旅行），则使用返回的ID
        val finalTripId = if (trip.id == 0L) tripId else trip.id

        // 保存所有位置
        trip.locations.forEach { location ->
            val locationWithTripId = location.copy(tripId = finalTripId)
            insertLocation(locationWithTripId)
        }

        return tripId
    }
    
    override suspend fun updateTrip(trip: Trip) {
        val tripEntity = trip.toEntity()
        tripDao.updateTrip(tripEntity)

        // 获取当前旅行的所有位置
        val currentLocations = tripDao.getLocationsByTripId(trip.id)

        // 删除当前所有位置，然后添加新的位置
        currentLocations.forEach { location ->
            tripDao.deleteLocationById(location.id)
        }

        // 添加所有新位置
        trip.locations.forEach { location ->
            insertLocation(location.copy(tripId = trip.id))
        }
    }
    
    override suspend fun deleteTrip(trip: Trip) {
        val tripEntity = trip.toEntity()
        tripDao.deleteTrip(tripEntity)
    }
    
    override suspend fun deleteTripById(id: Long) {
        tripDao.deleteTripById(id)
    }
    
    override suspend fun insertLocation(location: Location) {
        val locationEntity = location.toEntity()
        tripDao.insertLocation(locationEntity)
    }
    
    override suspend fun updateLocation(location: Location) {
        val locationEntity = location.toEntity()
        tripDao.updateLocation(locationEntity)
    }
    
    override suspend fun deleteLocation(location: Location) {
        val locationEntity = location.toEntity()
        tripDao.deleteLocation(locationEntity)
    }
    
    override fun searchTrips(query: String): Flow<List<Trip>> {
        return tripDao.searchTrips(query).map { tripEntities ->
            tripEntities.map { it.toDomainModel() }
        }
    }
}

// 扩展函数：在TripEntity和Trip之间转换
fun TripEntity.toDomainModel(): Trip {
    return Trip(
        id = this.id,
        title = this.title,
        description = this.description,
        date = this.date,
        photoPath = this.photoPath,
        locations = emptyList(), // 在TripEntity转Trip时，locations为空，因为需要通过TripWithLocations来获取
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Trip.toEntity(): TripEntity {
    return TripEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        date = this.date,
        photoPath = this.photoPath,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun LocationEntity.toDomainModel(): Location {
    return Location(
        id = this.id,
        tripId = this.tripId,
        locationName = this.locationName,
        latitude = this.latitude,
        longitude = this.longitude,
        note = this.note,
        visitedDate = this.visitedDate,
        createdAt = this.createdAt
    )
}

fun Location.toEntity(): LocationEntity {
    return LocationEntity(
        id = this.id,
        tripId = this.tripId,
        locationName = this.locationName,
        latitude = this.latitude,
        longitude = this.longitude,
        note = this.note,
        visitedDate = this.visitedDate,
        createdAt = this.createdAt
    )
}

fun TripWithLocations.toDomainModel(): Trip {
    val trip = this.trip.toDomainModel()
    return trip.copy(locations = this.locations.map { it.toDomainModel() })
}
package com.example.traveljournal.domain.repository

import com.example.traveljournal.domain.model.Location
import com.example.traveljournal.domain.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getAllTrips(): Flow<List<Trip>>
    fun getTripById(id: Long): Flow<Trip?>
    suspend fun insertTrip(trip: Trip): Long
    suspend fun updateTrip(trip: Trip)
    suspend fun deleteTrip(trip: Trip)
    suspend fun deleteTripById(id: Long)
    suspend fun insertLocation(location: Location)
    suspend fun updateLocation(location: Location)
    suspend fun deleteLocation(location: Location)
    fun searchTrips(query: String): Flow<List<Trip>>
}
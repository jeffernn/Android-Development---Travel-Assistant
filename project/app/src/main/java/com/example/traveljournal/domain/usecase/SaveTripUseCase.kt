package com.example.traveljournal.domain.usecase

import com.example.traveljournal.domain.model.Trip
import com.example.traveljournal.domain.repository.TripRepository
import javax.inject.Inject

class SaveTripUseCase @Inject constructor(
    private val repository: TripRepository
) {
    suspend operator fun invoke(trip: Trip): Long {
        return if (trip.id == 0L) {
            repository.insertTrip(trip)
        } else {
            repository.updateTrip(trip)
            trip.id
        }
    }
}
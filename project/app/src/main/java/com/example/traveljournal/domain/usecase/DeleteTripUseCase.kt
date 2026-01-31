package com.example.traveljournal.domain.usecase

import com.example.traveljournal.domain.model.Trip
import com.example.traveljournal.domain.repository.TripRepository
import javax.inject.Inject

class DeleteTripUseCase @Inject constructor(
    private val repository: TripRepository
) {
    suspend operator fun invoke(trip: Trip) {
        repository.deleteTrip(trip)
    }
}
package com.example.traveljournal.domain.usecase

import com.example.traveljournal.domain.model.Trip
import com.example.traveljournal.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchTripsUseCase @Inject constructor(
    private val repository: TripRepository
) {
    operator fun invoke(query: String): Flow<List<Trip>> {
        return repository.searchTrips(query)
    }
}
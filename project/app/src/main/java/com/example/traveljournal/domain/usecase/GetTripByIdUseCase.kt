package com.example.traveljournal.domain.usecase

import com.example.traveljournal.domain.model.Trip
import com.example.traveljournal.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTripByIdUseCase @Inject constructor(
    private val repository: TripRepository
) {
    operator fun invoke(id: Long): Flow<Trip?> {
        return repository.getTripById(id)
    }
}
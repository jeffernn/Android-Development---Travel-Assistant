package com.example.traveljournal.di

import com.example.traveljournal.data.local.database.dao.TripDao
import com.example.traveljournal.data.remote.RemoteDataSource
import com.example.traveljournal.data.remote.api.ExchangeRateApiService
import com.example.traveljournal.data.remote.api.GeocodeApiService
import com.example.traveljournal.data.remote.api.WeatherApiService
import com.example.traveljournal.data.repository.TripRepositoryImpl
import com.example.traveljournal.domain.repository.TripRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTripRepository(
        tripDao: TripDao
    ): TripRepository {
        return TripRepositoryImpl(tripDao)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        geocodeApiService: GeocodeApiService,
        exchangeRateApiService: ExchangeRateApiService,
        weatherApiService: WeatherApiService
    ): RemoteDataSource {
        return RemoteDataSource(geocodeApiService, exchangeRateApiService, weatherApiService)
    }

}
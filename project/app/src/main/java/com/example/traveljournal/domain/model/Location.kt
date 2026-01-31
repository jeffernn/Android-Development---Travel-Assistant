package com.example.traveljournal.domain.model

data class Location(
    val id: Long = 0,
    val tripId: Long,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val note: String = "",
    val visitedDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
package com.example.traveljournal.domain.model

data class Trip(
    val id: Long = 0,
    val title: String,
    val description: String,
    val date: String,
    val photoPath: String?,
    val locations: List<Location> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
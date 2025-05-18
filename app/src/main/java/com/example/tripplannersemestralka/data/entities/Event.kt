package com.example.tripplannersemestralka.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tripId: Int,
    val name: String,
    val description: String?,
    val startDateTime: LocalDateTime,
    val durationInMinutes: Int
)

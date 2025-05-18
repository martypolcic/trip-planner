package com.example.tripplannersemestralka.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val startDate: LocalDate,
    val startTime: LocalTime?,
    val endDate: LocalDate
)

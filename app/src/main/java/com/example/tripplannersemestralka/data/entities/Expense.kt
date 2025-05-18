package com.example.tripplannersemestralka.data.entities

import androidx.room.*
import java.time.LocalDateTime

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Participant::class,
            parentColumns = ["id"],
            childColumns = ["paidBy"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val amount: Double,
    val description: String? = null,
    val isResolved: Boolean = false,
    val dateTime: LocalDateTime,
    val tripId: Int,
    val paidBy: Int,
    val divisionMode: String = "Equally among all participants"
)

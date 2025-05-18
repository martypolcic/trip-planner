package com.example.tripplannersemestralka.data.dao

import androidx.room.*
import com.example.tripplannersemestralka.data.entities.Trip
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Query("SELECT * FROM trips ORDER BY startDate ASC")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("SELECT name FROM trips WHERE id = :tripId")
    suspend fun getTripNameById(tripId: Int): String?

    @Query("SELECT startDate FROM trips WHERE id = :tripId")
    suspend fun getTripStartDateById(tripId: Int): LocalDate?

    @Query("SELECT endDate FROM trips WHERE id = :tripId")
    suspend fun getTripEndDateById(tripId: Int): LocalDate?
}

package com.example.tripplannersemestralka.data.dao

import androidx.room.*
import com.example.tripplannersemestralka.data.entities.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("SELECT * FROM events WHERE tripId = :eventId LIMIT 1")
    fun getEventById(eventId: Int): Flow<Event?>

    @Query("SELECT * FROM events WHERE tripId = :tripId ORDER BY startDateTime ASC")
    fun getEventsForTrip(tripId: Int): Flow<List<Event>>
}

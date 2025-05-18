package com.example.tripplannersemestralka.data.dao

import androidx.room.*
import com.example.tripplannersemestralka.data.entities.Participant
import kotlinx.coroutines.flow.Flow

@Dao
interface ParticipantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: Participant)

    @Update
    suspend fun updateParticipant(participant: Participant)

    @Delete
    suspend fun deleteParticipant(participant: Participant)

    @Query("SELECT * FROM participants WHERE tripId = :tripId ORDER BY name ASC")
    fun getParticipantsForTrip(tripId: Int): Flow<List<Participant>>
}

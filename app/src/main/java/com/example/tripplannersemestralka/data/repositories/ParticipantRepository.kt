package com.example.tripplannersemestralka.data.repositories

import com.example.tripplannersemestralka.data.dao.ParticipantDao
import com.example.tripplannersemestralka.data.entities.Participant
import kotlinx.coroutines.flow.Flow

class ParticipantRepository(private val participantDao: ParticipantDao) {

    suspend fun insertParticipant(participant: Participant) {
        participantDao.insertParticipant(participant)
    }

    suspend fun updateParticipant(participant: Participant) {
        participantDao.updateParticipant(participant)
    }

    suspend fun deleteParticipant(participant: Participant) {
        participantDao.deleteParticipant(participant)
    }

    fun getParticipantsForTrip(tripId: Int): Flow<List<Participant>> =
        participantDao.getParticipantsForTrip(tripId)
}

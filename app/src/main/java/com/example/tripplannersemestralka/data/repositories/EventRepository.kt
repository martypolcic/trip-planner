package com.example.tripplannersemestralka.data.repositories

import com.example.tripplannersemestralka.data.dao.EventDao
import com.example.tripplannersemestralka.data.entities.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class EventRepository(private val eventDao: EventDao) {

    suspend fun insertEvent(event: Event) {
        eventDao.insertEvent(event)
    }

    suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event)
    }

    suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event)
    }

    suspend fun getEventById(eventId: Int): Event? {
        return eventDao.getEventById(eventId).firstOrNull()
    }

    fun getEventsForTrip(tripId: Int): Flow<List<Event>> {
        return eventDao.getEventsForTrip(tripId)
    }
}

package com.example.tripplannersemestralka.data.repositories

import com.example.tripplannersemestralka.data.dao.TripDao
import com.example.tripplannersemestralka.data.entities.Trip
import kotlinx.coroutines.flow.Flow

class TripRepository(private val tripDao: TripDao) {

    suspend fun insertTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    suspend fun updateTrip(trip: Trip) {
        tripDao.updateTrip(trip)
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }

    fun getAllTrips(): Flow<List<Trip>> = tripDao.getAllTrips()
}

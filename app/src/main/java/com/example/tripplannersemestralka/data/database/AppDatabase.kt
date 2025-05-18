package com.example.tripplannersemestralka.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tripplannersemestralka.data.dao.EventDao
import com.example.tripplannersemestralka.data.dao.ExpenseDao
import com.example.tripplannersemestralka.data.dao.ExpenseParticipantDao
import com.example.tripplannersemestralka.data.dao.ParticipantDao
import com.example.tripplannersemestralka.data.dao.TripDao
import com.example.tripplannersemestralka.data.entities.Event
import com.example.tripplannersemestralka.data.entities.Expense
import com.example.tripplannersemestralka.data.entities.ExpenseParticipant
import com.example.tripplannersemestralka.data.entities.Participant
import com.example.tripplannersemestralka.data.entities.Trip
import com.example.tripplannersemestralka.util.Converters

@Database(
    entities = [Trip::class, Participant::class, Event::class, Expense::class, ExpenseParticipant::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao
    abstract fun participantDao(): ParticipantDao
    abstract fun eventDao(): EventDao
    abstract fun expenseDao(): ExpenseDao
    abstract  fun expenseParticipantDao(): ExpenseParticipantDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trip_planner_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

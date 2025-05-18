package com.example.tripplannersemestralka.data.dao

import androidx.room.*
import com.example.tripplannersemestralka.data.entities.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses WHERE tripId = :tripId ORDER BY dateTime ASC")
    fun getExpensesForTrip(tripId: Int): Flow<List<Expense>>

    @Query("""
        SELECT e.* FROM expenses e
        INNER JOIN expense_participants ep ON e.id = ep.expenseId
        WHERE ep.participantId = :participantId
    """)
    fun getExpensesForParticipant(participantId: Int): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)
}

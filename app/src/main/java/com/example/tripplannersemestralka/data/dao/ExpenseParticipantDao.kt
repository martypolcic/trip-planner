package com.example.tripplannersemestralka.data.dao

import androidx.room.*
import com.example.tripplannersemestralka.data.entities.ExpenseParticipant
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseParticipantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseParticipant(expenseParticipant: ExpenseParticipant)

    @Query("SELECT * FROM expense_participants WHERE expenseId = :expenseId")
    fun getParticipantsForExpense(expenseId: Int): Flow<List<ExpenseParticipant>>

    @Query("SELECT * FROM expense_participants WHERE participantId = :participantId")
    fun getExpensesForParticipant(participantId: Int): Flow<List<ExpenseParticipant>>

    @Delete
    suspend fun deleteExpenseParticipant(expenseParticipant: ExpenseParticipant)

    @Query("DELETE FROM expense_participants WHERE expenseId = :expenseId")
    suspend fun deleteParticipantsForExpense(expenseId: Int)
}

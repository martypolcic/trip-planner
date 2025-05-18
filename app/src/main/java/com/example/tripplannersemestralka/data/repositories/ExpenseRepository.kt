package com.example.tripplannersemestralka.data.repositories

import com.example.tripplannersemestralka.data.dao.ExpenseDao
import com.example.tripplannersemestralka.data.dao.ExpenseParticipantDao
import com.example.tripplannersemestralka.data.entities.Expense
import com.example.tripplannersemestralka.data.entities.ExpenseParticipant
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val expenseParticipantDao: ExpenseParticipantDao
) {

    suspend fun insertExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense)
    }

    suspend fun insertExpenseParticipant(expenseParticipant: ExpenseParticipant) {
        expenseParticipantDao.insertExpenseParticipant(expenseParticipant)
    }

    fun getExpensesForTrip(tripId: Int): Flow<List<Expense>> {
        return expenseDao.getExpensesForTrip(tripId)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
        expenseParticipantDao.deleteParticipantsForExpense(expense.id)
    }
}

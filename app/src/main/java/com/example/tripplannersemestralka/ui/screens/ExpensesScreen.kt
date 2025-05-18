package com.example.tripplannersemestralka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tripplannersemestralka.data.database.AppDatabase
import com.example.tripplannersemestralka.data.entities.Expense
import com.example.tripplannersemestralka.data.repositories.ExpenseRepository
import kotlinx.coroutines.launch

@Composable
fun ExpensesScreen(
    navController: NavController,
    tripId: Int
) {
    val db = AppDatabase.getInstance(navController.context)
    val expenseRepository = ExpenseRepository(db.expenseDao(), db.expenseParticipantDao())

    var expenses by remember { mutableStateOf(emptyList<Expense>()) }
    var isEditing by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(tripId) {
        expenseRepository.getExpensesForTrip(tripId).collect { expenseList ->
            expenses = expenseList
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Expenses", fontSize = 24.sp, color = Color.Black)

            IconButton(onClick = { isEditing = !isEditing }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Toggle Edit Mode",
                    tint = if (isEditing) Color.Red else Color.Black
                )
            }
        }

        // Expense List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(expenses) { expense ->
                ExpenseCard(
                    expense = expense,
                    isEditing = isEditing,
                    onEditClick = {
                        navController.navigate("createExpenseForm/$tripId/${expense.id}")
                    },
                    onDeleteClick = {
                        expenseToDelete = expense
                    }
                )
            }
        }
    }

    // Delete Confirmation Dialog
    expenseToDelete?.let { expense ->
        DeleteConfirmationDialog(
            expense = expense,
            onConfirm = {
                val expenseToRemove = expense // Capture the expense object before coroutine launch
                coroutineScope.launch {
                    expenseRepository.deleteExpense(expenseToRemove)
                }
                expenseToDelete = null
            },
            onCancel = {
                expenseToDelete = null
            }
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    expense: Expense,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text(text = "Delete Expense?") },
        text = {
            Text(
                text = "Are you sure you want to delete '${expense.name}'? This action cannot be undone.",
                fontSize = 16.sp
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun ExpenseCard(
    expense: Expense,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = expense.name, fontSize = 18.sp)

                if (isEditing) {
                    Row {
                        IconButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit",
                                tint = Color.Blue
                            )
                        }

                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Amount: ${expense.amount} €",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Text(
                text = "Date: ${expense.dateTime.toLocalDate()}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}


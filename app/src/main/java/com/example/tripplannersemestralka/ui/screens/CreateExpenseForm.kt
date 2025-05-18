package com.example.tripplannersemestralka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tripplannersemestralka.data.database.AppDatabase
import com.example.tripplannersemestralka.data.entities.Expense
import com.example.tripplannersemestralka.data.entities.ExpenseParticipant
import com.example.tripplannersemestralka.data.entities.Participant
import com.example.tripplannersemestralka.data.repositories.ExpenseRepository
import com.example.tripplannersemestralka.data.repositories.ParticipantRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun CreateExpenseForm(
    navController: NavController,
    tripId: Int,
    expenseId: Int? = null
) {
    val db = AppDatabase.getInstance(navController.context)
    val expenseRepository = ExpenseRepository(db.expenseDao(), db.expenseParticipantDao())
    val participantRepository = ParticipantRepository(db.participantDao())

    var expenseName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var paidById by remember { mutableStateOf<Int?>(null) }
    var selectedParticipants by remember { mutableStateOf(emptyList<Int>()) }
    val divisionMode by remember { mutableStateOf("Equally among all participants") }

    var participants by remember { mutableStateOf(emptyList<Participant>()) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(tripId) {
        participantRepository.getParticipantsForTrip(tripId).collect { participantList ->
            participants = participantList
        }
    }

    var showPaidByDropdown by remember { mutableStateOf(false) }
    var showParticipantsList by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Create Expense", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = expenseName,
            onValueChange = { expenseName = it },
            label = { Text("Expense Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        // Paid By Selection with Column Wrapper
        Column {
            Text(text = "Paid By", modifier = Modifier.padding(bottom = 4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPaidByDropdown = !showPaidByDropdown }
                    .padding(vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = participants.find { it.id == paidById }?.name ?: "Select a Participant",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        imageVector = if (showPaidByDropdown) Icons.Filled.KeyboardArrowUp else Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                }
            }

            if (showPaidByDropdown) {
                DropdownMenu(
                    expanded = showPaidByDropdown,
                    onDismissRequest = { showPaidByDropdown = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    participants.forEach { participant ->
                        DropdownMenuItem(
                            text = { Text(participant.name) },
                            onClick = {
                                paidById = participant.id
                                showPaidByDropdown = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Participants Selection with Expandable List
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showParticipantsList = !showParticipantsList }
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Participants")
            Icon(
                imageVector = if (showParticipantsList) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropDown,
                contentDescription = "Expand/Collapse"
            )
        }

        if (showParticipantsList) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
            ) {
                items(participants) { participant ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedParticipants.contains(participant.id),
                            onCheckedChange = { isChecked ->
                                selectedParticipants = if (isChecked) {
                                    selectedParticipants + participant.id
                                } else {
                                    selectedParticipants - participant.id
                                }
                            }
                        )
                        Text(text = participant.name)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull() ?: 0.0

                coroutineScope.launch {
                    val expense = Expense(
                        name = expenseName,
                        description = description,
                        amount = amountValue,
                        dateTime = LocalDateTime.now(),
                        tripId = tripId,
                        paidBy = paidById ?: participants.firstOrNull()?.id ?: -1,
                        divisionMode = divisionMode
                    )

                    val newExpenseId = expenseRepository.insertExpense(expense).toInt()

                    selectedParticipants.forEach { participantId ->
                        expenseRepository.insertExpenseParticipant(
                            ExpenseParticipant(expenseId = newExpenseId, participantId = participantId)
                        )
                    }
                }
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Expense")
        }
    }
}

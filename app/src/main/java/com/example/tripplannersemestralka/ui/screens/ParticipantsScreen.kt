package com.example.tripplannersemestralka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tripplannersemestralka.data.database.AppDatabase
import com.example.tripplannersemestralka.data.entities.Participant
import com.example.tripplannersemestralka.data.repositories.ParticipantRepository
import kotlinx.coroutines.launch

@Composable
fun ParticipantsScreen(
    navController: NavController,
    tripId: Int?
) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val participantRepository = ParticipantRepository(db.participantDao())

    var participants by remember { mutableStateOf(listOf<Participant>()) }
    var isEditing by remember { mutableStateOf(false) }
    var showAddParticipantDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(tripId) {
        tripId?.let {
            participantRepository.getParticipantsForTrip(it).collect { participantList ->
                participants = participantList
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Participants",
                    fontSize = 24.sp,
                    modifier = Modifier.weight(1f),
                    color = Color.Black
                )

                IconButton(onClick = { isEditing = !isEditing }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Mode",
                        tint = if (isEditing) Color.Red else Color.Black
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(participants) { participant ->
                    ParticipantCard(
                        participant = participant,
                        isEditing = isEditing,
                        onDelete = { participantToDelete ->
                            coroutineScope.launch {
                                participantRepository.deleteParticipant(participantToDelete)
                            }
                        },
                        onRename = { newName ->
                            coroutineScope.launch {
                                val updatedParticipant = participant.copy(name = newName)
                                participantRepository.updateParticipant(updatedParticipant)
                            }
                        }
                    )
                }
            }
        }

        // Floating Action Button for Adding Participant
        FloatingActionButton(
            onClick = { showAddParticipantDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color.Blue,
            contentColor = Color.White
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Participant")
        }
    }

    if (showAddParticipantDialog && tripId != null) {
        AddParticipantDialog(
            tripId = tripId,
            participantRepository = participantRepository,
            onDismiss = { showAddParticipantDialog = false }
        )
    }
}

@Composable
fun AddParticipantDialog(
    tripId: Int,
    participantRepository: ParticipantRepository,
    onDismiss: () -> Unit
) {
    var newParticipantName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add New Participant") },
        text = {
            TextField(
                value = newParticipantName,
                onValueChange = { newParticipantName = it },
                placeholder = { Text("Enter participant name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (newParticipantName.isNotBlank()) {
                        coroutineScope.launch {
                            participantRepository.insertParticipant(
                                Participant(tripId = tripId, name = newParticipantName)
                            )
                        }
                        keyboardController?.hide()
                        onDismiss()
                    }
                })
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newParticipantName.isNotBlank()) {
                        coroutineScope.launch {
                            participantRepository.insertParticipant(
                                Participant(tripId = tripId, name = newParticipantName)
                            )
                        }
                        keyboardController?.hide()
                        onDismiss()
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ParticipantCard(
    participant: Participant,
    isEditing: Boolean,
    onDelete: (Participant) -> Unit,
    onRename: (String) -> Unit
) {
    var isRenaming by remember { mutableStateOf(false) }
    var newParticipantName by remember { mutableStateOf(participant.name) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(participant.id, isRenaming) {
        if (!isRenaming) {
            newParticipantName = participant.name // Reset input field when renaming is completed
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isRenaming) {
                TextField(
                    value = newParticipantName,
                    onValueChange = { newParticipantName = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (newParticipantName.isNotBlank() && newParticipantName != participant.name) {
                            isRenaming = false
                            onRename(newParticipantName)
                            keyboardController?.hide()
                        }
                    }),
                    modifier = Modifier.weight(1f)
                )
            } else {
                Text(text = participant.name, fontSize = 18.sp, modifier = Modifier.weight(1f))
            }

            if (isEditing) {
                IconButton(onClick = {
                    if (isRenaming) {
                        if (newParticipantName.isNotBlank() && newParticipantName != participant.name) {
                            isRenaming = false
                            onRename(newParticipantName)
                            keyboardController?.hide()
                        }
                    } else {
                        isRenaming = true
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = if (isRenaming) "Submit" else "Edit",
                        tint = if (isRenaming) Color.Green else Color.Black
                    )
                }

                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Participant") },
            text = { Text("Are you sure you want to delete this participant? This action is irreversible.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(participant)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

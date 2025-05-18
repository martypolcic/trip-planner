package com.example.tripplannersemestralka.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripplannersemestralka.data.entities.Event
import com.example.tripplannersemestralka.data.repositories.EventRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun EditEventDialog(
    event: Event,
    onDismiss: () -> Unit,
    eventRepository: EventRepository
) {
    val context = LocalContext.current

    var eventName by remember { mutableStateOf(event.name) }
    var eventDescription by remember { mutableStateOf(event.description ?: "") }

    var startDate by remember { mutableStateOf(event.startDateTime.toLocalDate()) }
    var startTime by remember { mutableStateOf(event.startDateTime.toLocalTime()) }

    val endDateTime = event.startDateTime.plusMinutes(event.durationInMinutes.toLong())
    var endDate by remember { mutableStateOf(endDateTime.toLocalDate()) }
    var endTime by remember { mutableStateOf(endDateTime.toLocalTime()) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Edit Event", fontSize = 20.sp)
                IconButton(onClick = { showDeleteConfirmation = true }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Event",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomInputField(
                    label = "Event Name",
                    value = eventName,
                    onValueChange = { eventName = it }
                )

                CustomInputField(
                    label = "Event Description (Optional)",
                    value = eventDescription,
                    onValueChange = { eventDescription = it }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomInputField(
                        label = "Start Date",
                        value = startDate.toString(),
                        isClickable = true,
                        icon = Icons.Filled.DateRange,
                        onClick = { showStartDatePicker = true },
                        onValueChange = {},
                        modifier = Modifier.weight(1f)
                    )

                    CustomInputField(
                        label = "Start Time",
                        value = "${startTime.hour}:${startTime.minute}",
                        isClickable = true,
                        icon = Icons.Filled.DateRange,
                        onClick = { showStartTimePicker = true },
                        onValueChange = {},
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomInputField(
                        label = "End Date",
                        value = endDate.toString(),
                        isClickable = true,
                        icon = Icons.Filled.DateRange,
                        onClick = { showEndDatePicker = true },
                        onValueChange = {},
                        modifier = Modifier.weight(1f)
                    )

                    CustomInputField(
                        label = "End Time",
                        value = "${endTime.hour}:${endTime.minute}",
                        isClickable = true,
                        icon = Icons.Filled.DateRange,
                        onClick = { showEndTimePicker = true },
                        onValueChange = {},
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val startDateTimeValue = LocalDateTime.of(startDate, startTime)
                val endDateTimeValue = LocalDateTime.of(endDate, endTime)

                val updatedEvent = event.copy(
                    name = eventName,
                    description = eventDescription.takeIf { it.isNotBlank() },
                    startDateTime = startDateTimeValue,
                    durationInMinutes = java.time.Duration.between(startDateTimeValue, endDateTimeValue).toMinutes().toInt()
                )

                coroutineScope.launch {
                    eventRepository.updateEvent(updatedEvent)
                }
                onDismiss()
            }) {
                Text("Update Event")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )

    if (showStartDatePicker) {
        DatePicker(
            context = context,
            selectedDate = startDate,
            onDateSelected = {
                startDate = it
                showStartDatePicker = false
            }
        )
    }

    if (showEndDatePicker) {
        DatePicker(
            context = context,
            selectedDate = endDate,
            onDateSelected = {
                endDate = it
                showEndDatePicker = false
            }
        )
    }

    if (showStartTimePicker) {
        TimePicker(
            context = context,
            selectedTime = startTime,
            onTimeSelected = {
                startTime = it
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        TimePicker(
            context = context,
            selectedTime = endTime,
            onTimeSelected = {
                endTime = it
                showEndTimePicker = false
            }
        )
    }

    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            onConfirm = {
                coroutineScope.launch {
                    eventRepository.deleteEvent(event)
                }
                onDismiss()
            },
            onCancel = { showDeleteConfirmation = false }
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = { Text("Delete Event?") },
        text = { Text("Are you sure you want to delete this event? This action cannot be undone.") },
        confirmButton = {
            Button(
                onClick = { onConfirm() },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text("Cancel")
            }
        }
    )
}

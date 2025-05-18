package com.example.tripplannersemestralka.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripplannersemestralka.data.entities.Event
import com.example.tripplannersemestralka.data.repositories.EventRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun AddEventDialog(
    tripId: Int?,
    onDismiss: () -> Unit,
    eventRepository: EventRepository
) {
    val context = LocalContext.current

    var eventName by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }

    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var startTime by remember { mutableStateOf(LocalTime.now()) }

    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var endTime by remember { mutableStateOf(LocalTime.now().plusHours(1)) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Create New Event", fontSize = 20.sp) },
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
                if (tripId != null && eventName.isNotBlank()) {
                    val startDateTime = LocalDateTime.of(startDate, startTime)
                    val endDateTime = LocalDateTime.of(endDate, endTime)

                    coroutineScope.launch {
                        eventRepository.insertEvent(
                            Event(
                                tripId = tripId,
                                name = eventName,
                                description = eventDescription,
                                startDateTime = startDateTime,
                                durationInMinutes = java.time.Duration.between(startDateTime, endDateTime).toMinutes().toInt()
                            )
                        )
                    }
                    onDismiss()
                }
            }) {
                Text("Create Event")
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

        LaunchedEffect(Unit) {
            showStartDatePicker = false
        }
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

        LaunchedEffect(Unit) {
            showEndDatePicker = false
        }
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

        LaunchedEffect(Unit) {
            showStartTimePicker = false
        }
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

        LaunchedEffect(Unit) {
            showEndTimePicker = false
        }
    }
}

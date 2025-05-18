package com.example.tripplannersemestralka.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import com.example.tripplannersemestralka.data.database.AppDatabase
import com.example.tripplannersemestralka.data.entities.Trip
import com.example.tripplannersemestralka.data.repositories.TripRepository
import com.example.tripplannersemestralka.ui.components.CustomInputField
import com.example.tripplannersemestralka.ui.components.DatePicker
import com.example.tripplannersemestralka.ui.components.TimePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun CreateTripForm(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val tripRepository = TripRepository(db.tripDao())

    var tripName by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var startTimeEnabled by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(LocalTime.now()) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Create a New Trip", fontSize = 24.sp)

        CustomInputField(
            label = "Trip Name",
            value = tripName,
            onValueChange = { tripName = it }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
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

            if (startTimeEnabled) {
                CustomInputField(
                    label = "Start Time",
                    value = "${startTime.hour}:${startTime.minute}",
                    isClickable = true,
                    icon = Icons.Filled.DateRange,
                    onClick = { showTimePicker = true },
                    onValueChange = {},
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Add Start Time")
            Switch(
                checked = startTimeEnabled,
                onCheckedChange = { startTimeEnabled = it }
            )
        }

        CustomInputField(
            label = "End Date",
            value = endDate.toString(),
            isClickable = true,
            icon = Icons.Filled.DateRange,
            onClick = { showEndDatePicker = true },
            onValueChange = {}
        )

        Button(
            onClick = {
                if (tripName.isNotBlank() && startDate <= endDate) {
                    val trip = Trip(
                        name = tripName,
                        startDate = startDate,
                        startTime = if (startTimeEnabled) startTime else null,
                        endDate = endDate
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        tripRepository.insertTrip(trip)
                    }

                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Trip")
        }
    }

    // Handle Start Date Picker
    if (showStartDatePicker) {
        DatePicker(
            context = context,
            selectedDate = startDate,
            onDateSelected = {
                startDate = it
                showStartDatePicker = false
            }
        )

        // Reset state after dialog is dismissed
        LaunchedEffect(Unit) {
            showStartDatePicker = false
        }
    }

    // Handle End Date Picker
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

    // Handle Time Picker
    if (showTimePicker) {
        TimePicker(
            context = context,
            selectedTime = startTime,
            onTimeSelected = {
                startTime = it
                showTimePicker = false
            }
        )

        LaunchedEffect(Unit) {
            showTimePicker = false
        }
    }
}

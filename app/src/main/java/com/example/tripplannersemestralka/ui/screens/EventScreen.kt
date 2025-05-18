package com.example.tripplannersemestralka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripplannersemestralka.data.database.AppDatabase
import com.example.tripplannersemestralka.data.entities.Event
import com.example.tripplannersemestralka.data.repositories.EventRepository
import com.example.tripplannersemestralka.ui.components.AddEventDialog
import com.example.tripplannersemestralka.ui.components.EditEventDialog
import java.time.LocalDate

import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun EventsScreen(
    tripId: Int?,
    tripStartDate: LocalDate,
    tripEndDate: LocalDate
) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val eventRepository = EventRepository(db.eventDao())

    var events by remember { mutableStateOf(listOf<Event>()) }
    var showEditEventDialog by remember { mutableStateOf<Event?>(null) }
    var showAddEventDialog by remember { mutableStateOf(false) }

    // Persist selected day using rememberSaveable
    var selectedDay by rememberSaveable { mutableStateOf(tripStartDate) }

    LaunchedEffect(tripId) {
        tripId?.let {
            eventRepository.getEventsForTrip(it).collect { eventList ->
                events = eventList
            }
        }
    }

    val daysInTrip = generateTripDays(tripStartDate, tripEndDate)

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Events", fontSize = 24.sp, color = Color.Black)
                Text(text = selectedDay.toString(), fontSize = 18.sp, color = Color.Gray)
            }

            // Day Selector
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(daysInTrip) { day ->
                        DayButton(
                            date = day,
                            isSelected = day == selectedDay,
                            onClick = { selectedDay = day }
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 1.dp, color = Color.Gray)

            // Scrollable Hourly View
            Box(modifier = Modifier.weight(1f)) {
                HourlyView(
                    events = events,
                    selectedDay = selectedDay,
                    onEventClick = { event ->
                        showEditEventDialog = event
                    }
                )
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddEventDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color.Blue,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Event")
        }
    }

    if (showAddEventDialog) {
        AddEventDialog(
            tripId = tripId,
            onDismiss = { showAddEventDialog = false },
            eventRepository = eventRepository
        )
    }

    if (showEditEventDialog != null) {
        EditEventDialog(
            event = showEditEventDialog!!,
            onDismiss = { showEditEventDialog = null },
            eventRepository = eventRepository
        )
    }
}

@Composable
fun DayButton(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color.Red else Color.DarkGray
    val textColor = if (isSelected) Color.White else Color.LightGray

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfWeek.name.take(3),
                fontSize = 12.sp,
                color = textColor
            )
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 16.sp,
                color = textColor
            )
        }
    }
}

fun generateTripDays(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
    val days = mutableListOf<LocalDate>()
    var currentDate = startDate

    while (!currentDate.isAfter(endDate)) {
        days.add(currentDate)
        currentDate = currentDate.plusDays(1)
    }

    return days
}

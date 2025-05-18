package com.example.tripplannersemestralka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripplannersemestralka.data.entities.Event
import com.example.tripplannersemestralka.ui.theme.EventColors
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun HourlyView(
    events: List<Event>,
    selectedDay: LocalDate,
    onEventClick: (Event) -> Unit
) {
    val hoursInDay = (0..23).toList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(hoursInDay) { hour ->
            HourSlot(
                hour = hour,
                events = events,
                selectedDay = selectedDay,
                onEventClick = onEventClick
            )
        }
    }
}

@Composable
fun HourSlot(
    hour: Int,
    events: List<Event>,
    selectedDay: LocalDate,
    onEventClick: (Event) -> Unit
) {
    val hourStart = LocalTime.of(hour, 0)
    val hourEnd = hourStart.plusHours(1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Text(
            text = if (hour == 0) "12 AM" else if (hour < 12) "$hour AM" else if (hour == 12) "12 PM" else "${hour - 12} PM",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 8.dp)
        )

        // Identify overlapping events
        val overlappingEvents = events.filter { event ->
            val eventStart = event.startDateTime.toLocalTime()
            val eventEnd = eventStart.plusMinutes(event.durationInMinutes.toLong())

            event.startDateTime.toLocalDate() == selectedDay &&
                    (hourStart <= eventEnd && hourEnd > eventStart)
        }

        overlappingEvents.forEachIndexed { index, event ->
            val eventColor = EventColors[event.id % EventColors.size]

            EventBlock(
                event = event,
                onClick = { onEventClick(event) },
                offset = 60.dp * index,
                color = eventColor
            )
        }
    }
}

@Composable
fun EventBlock(
    event: Event,
    onClick: () -> Unit,
    offset: Dp,
    color: Color
) {
    val eventStart = event.startDateTime.toLocalTime()
    val eventEnd = eventStart.plusMinutes(event.durationInMinutes.toLong())
    val eventDuration = java.time.Duration.between(eventStart, eventEnd).toMinutes()

    val durationFraction = (eventDuration / 60f).coerceAtMost(1f)

    Card(
        modifier = Modifier
            .fillMaxWidth(durationFraction)
            .padding(start = 60.dp + offset)
            .height(60.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color.Gray
        )
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            // Top Border Highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(color)
                    .align(Alignment.TopStart)
            )

            Text(
                text = event.name,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterStart)
                    .background(Color.Transparent)
            )
        }
    }
}

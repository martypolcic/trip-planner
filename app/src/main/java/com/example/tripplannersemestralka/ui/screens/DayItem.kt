package com.example.tripplannersemestralka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun DayItem(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(
                color = if (isSelected) Color.Blue else Color.LightGray,
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = if (isSelected) Color.DarkGray else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfWeek.name.substring(0, 3),
                fontSize = 12.sp,
                color = Color.White
            )
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

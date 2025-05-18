package com.example.tripplannersemestralka.ui.components

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.runtime.Composable
import java.time.LocalTime

@Composable
fun TimePicker(
    context: Context,
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit
) {
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeSelected(LocalTime.of(hourOfDay, minute))
        },
        selectedTime.hour,
        selectedTime.minute,
        true
    )

    timePickerDialog.show()
}

package com.example.tripplannersemestralka.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import com.example.tripplannersemestralka.data.database.AppDatabase
import kotlinx.coroutines.launch

@Composable
fun BottomNavBar(
    navController: NavController,
    selectedTripId: Int?,
    onTripSelectionClick: () -> Unit
) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    var tripName by remember { mutableStateOf<String?>(null) }
    var tripStartDate by remember { mutableStateOf<String?>(null) }
    var tripEndDate by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedTripId) {
        coroutineScope.launch {
            if (selectedTripId != null) {
                tripName = db.tripDao().getTripNameById(selectedTripId)
                tripStartDate = db.tripDao().getTripStartDateById(selectedTripId)?.toString()
                tripEndDate = db.tripDao().getTripEndDateById(selectedTripId)?.toString()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Header with Trip Name or Selection Prompt
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(vertical = 8.dp)
                .clickable { onTripSelectionClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = tripName ?: "Pick a Trip to Modify",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }

        // Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trip Selection Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = "Trips",
                tint = Color.White,
                modifier = Modifier.clickable {
                    navController.navigate("tripSelection") {
                        popUpTo("tripSelection") { inclusive = true }
                    }
                }
            )

            if (selectedTripId != null && tripStartDate != null && tripEndDate != null) {

                // Participants Icon
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Participants",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        navController.navigate("participants/$selectedTripId")
                    }
                )

                // Events Icon
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Events",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        navController.navigate("events/$selectedTripId/$tripStartDate/$tripEndDate")
                    }
                )

                // Expenses Icon
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Expenses",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        if (selectedTripId != null) {
                            navController.navigate("expenses/$selectedTripId")
                        }
                    }
                )
            }
        }
    }
}

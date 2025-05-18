package com.example.tripplannersemestralka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tripplannersemestralka.data.database.AppDatabase
import com.example.tripplannersemestralka.data.entities.Trip
import com.example.tripplannersemestralka.data.repositories.TripRepository
import kotlinx.coroutines.launch

@Composable
fun TripSelectionScreen(
    navController: NavController,
    onTripSelected: (Int?) -> Unit
) {
    val context = LocalContext.current
    val db = AppDatabase.getInstance(context)
    val tripRepository = TripRepository(db.tripDao())

    var trips by remember { mutableStateOf(listOf<Trip>()) }
    var isEditing by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        tripRepository.getAllTrips().collect { tripList ->
            trips = tripList
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
                    text = "Select a Trip",
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(trips) { trip ->
                    TripCard(
                        trip = trip,
                        isEditing = isEditing,
                        onSelect = {
                            onTripSelected(trip.id)
                            navController.navigate("landingPage") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { navController.navigate("createTripForm") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color.Blue,
            contentColor = Color.White
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Create New Trip")
        }
    }
}

@Composable
fun TripCard(
    trip: Trip,
    isEditing: Boolean,
    onSelect: () -> Unit
) {
    var isRenaming by remember { mutableStateOf(false) }
    var newTripName by remember { mutableStateOf(trip.name) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(120.dp)
            .clickable {
                if (!isRenaming) {
                    onSelect()
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFECEFF1))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Section - Trip Name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = trip.name.ifBlank { "No Name" },
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )

                if (isEditing) {
                    IconButton(onClick = { isRenaming = !isRenaming }) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Rename")
                    }
                }
            }

            Divider(color = Color.Gray, thickness = 1.dp)

            // Bottom Section - Dates
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Start: ${trip.startDate} - End: ${trip.endDate}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

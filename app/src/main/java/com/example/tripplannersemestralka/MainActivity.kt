package com.example.tripplannersemestralka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.tripplannersemestralka.navigation.NavGraph
import com.example.tripplannersemestralka.ui.components.BottomNavBar
import com.example.tripplannersemestralka.ui.theme.TripPlannerSemestralkaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TripPlannerSemestralkaTheme {
                val navController = rememberNavController()
                var currentTripId by remember { mutableStateOf<Int?>(null) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavBar(
                            navController = navController,
                            selectedTripId = currentTripId,
                            onTripSelectionClick = {
                                navController.navigate("tripSelection")
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavGraph(
                            navController = navController,
                            onTripSelected = { selectedTripId ->
                                currentTripId = selectedTripId
                            },
                            selectedTripId = currentTripId
                        )
                    }
                }
            }
        }
    }
}

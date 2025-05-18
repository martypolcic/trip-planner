package com.example.tripplannersemestralka.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LandingPage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EncouragingButton(onClick = {
            navController.navigate("createTripForm")
        })
    }
}

@Composable
fun EncouragingButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = "Feeling Bored? Plan a New Adventure!")
    }
}

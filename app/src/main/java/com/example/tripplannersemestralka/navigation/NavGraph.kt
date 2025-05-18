package com.example.tripplannersemestralka.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.tripplannersemestralka.ui.screens.CreateExpenseForm
import com.example.tripplannersemestralka.ui.screens.LandingPage
import com.example.tripplannersemestralka.ui.screens.TripSelectionScreen
import com.example.tripplannersemestralka.ui.screens.CreateTripForm
import com.example.tripplannersemestralka.ui.screens.ParticipantsScreen
import com.example.tripplannersemestralka.ui.screens.EventsScreen
import com.example.tripplannersemestralka.ui.screens.ExpensesScreen
import java.time.LocalDate

@Composable
fun NavGraph(
    navController: NavHostController,
    onTripSelected: (Int?) -> Unit,
    selectedTripId: Int?
) {
    NavHost(navController = navController, startDestination = "landingPage") {

        composable("landingPage") {
            LandingPage(navController = navController)
        }

        composable("tripSelection") {
            TripSelectionScreen(
                navController = navController,
                onTripSelected = { tripId ->
                    onTripSelected(tripId)
                }
            )
        }

        composable("createTripForm") {
            CreateTripForm(navController = navController)
        }

        composable(
            route = "participants/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId")
            ParticipantsScreen(
                navController = navController,
                tripId = tripId
            )
        }

        composable(
            route = "events/{tripId}/{startDate}/{endDate}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.IntType },
                navArgument("startDate") { type = NavType.StringType },
                navArgument("endDate") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId")
            val startDate = backStackEntry.arguments?.getString("startDate")
            val endDate = backStackEntry.arguments?.getString("endDate")

            if (tripId != null && startDate != null && endDate != null) {
                EventsScreen(
                    tripId = tripId,
                    tripStartDate = LocalDate.parse(startDate),
                    tripEndDate = LocalDate.parse(endDate)
                )
            }
        }

        composable(
            route = "expenses/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.IntType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId")
            ExpensesScreen(navController = navController, tripId = tripId ?: -1)
        }

        composable(
            route = "createExpenseForm/{tripId}?expenseId={expenseId}",
            arguments = listOf(
                navArgument("tripId") { type = NavType.IntType },
                navArgument("expenseId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getInt("tripId") ?: -1
            val expenseIdString = backStackEntry.arguments?.getString("expenseId")
            val expenseId = expenseIdString?.toIntOrNull()

            CreateExpenseForm(
                navController = navController,
                tripId = tripId,
                expenseId = expenseId
            )
        }

    }
}

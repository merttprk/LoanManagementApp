package com.loanmanagementapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.loanmanagementapp.domain.repository.LoanRepository
import com.loanmanagementapp.presentation.ui.dashboard.HomeScreen

@Composable
fun NavGraph(
    navController: NavHostController, 
    repository: LoanRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(repository)
        }
        composable(Screen.LoanDetails.route) {
            // Geçici olarak HomeScreen kullanılıyor, daha sonra LoanDetailsScreen eklenecek
            HomeScreen(repository)
        }
        composable(Screen.LoanApplication.route) {
            // Geçici olarak HomeScreen kullanılıyor, daha sonra LoanApplicationScreen eklenecek
            HomeScreen(repository)
        }
    }
}
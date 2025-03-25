package com.loanmanagementapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.loanmanagementapp.presentation.ui.auth.LoginScreen
import com.loanmanagementapp.presentation.ui.dashboard.HomeScreen
import com.loanmanagementapp.presentation.ui.loans.LoanApplicationScreen
import com.loanmanagementapp.presentation.ui.loans.LoanDetailsScreen
import com.loanmanagementapp.presentation.ui.loans.LoanCalculationScreen

@Composable
fun NavGraph(
    navController: NavHostController, 
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Giriş Ekranı
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Ana Sayfa
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToLoanDetails = { loanId ->
                    navController.navigate("${Screen.LoanDetails.route}/$loanId")
                },
                onNavigateToLoanApplication = {
                    navController.navigate(Screen.LoanApplication.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        // Kredi Detayları
        composable(
            route = "${Screen.LoanDetails.route}/{loanId}",
            arguments = listOf(navArgument("loanId") { type = NavType.StringType })
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getString("loanId") ?: ""
            LoanDetailsScreen(
                loanId = loanId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Kredi Başvurusu
        composable(Screen.LoanApplication.route) {
            LoanApplicationScreen(
                onNavigateBack = { navController.popBackStack() },
                onApplicationSubmitted = { navController.popBackStack() }
            )
        }
        
        // Kredi Hesaplama
        composable(Screen.LoanCalculator.route) {
            LoanCalculationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
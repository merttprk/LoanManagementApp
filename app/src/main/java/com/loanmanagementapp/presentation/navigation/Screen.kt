package com.loanmanagementapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    data object Home : Screen("home", "Ana Ekran", Icons.Filled.Home, Icons.Outlined.Home)
    data object LoanDetails : Screen("loan_details", "Kredi Detayları", Icons.Filled.Info, Icons.Outlined.Info)
    data object LoanApplication : Screen("loan_application", "Kredi Başvuru", Icons.Filled.Create, Icons.Outlined.Create)
    data object LoanCalculator : Screen("loan_calculator", "Kredi Hesaplama", Icons.Filled.Search, Icons.Outlined.Search)

    // List of all bottom navigation items
    companion object {
        val bottomNavItems = listOf(Home, LoanApplication,LoanCalculator,)
    }
}
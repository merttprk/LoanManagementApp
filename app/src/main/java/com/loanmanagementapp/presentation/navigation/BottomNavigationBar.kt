package com.loanmanagementapp.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.loanmanagementapp.theme.Blue80
import com.loanmanagementapp.theme.LightBlue80
import com.loanmanagementapp.theme.White

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Blue80
    ) {
        Screen.bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == screen.route) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.title,
                        tint = White
                    )
                },
                label = { Text(screen.title, color = White) },
                selected = currentRoute == screen.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = White,
                    unselectedIconColor = White.copy(alpha = 0.6f),
                    selectedTextColor = White,
                    unselectedTextColor = White.copy(alpha = 0.6f),
                    indicatorColor = LightBlue80
                ),
                onClick = {
                    navController.navigate(screen.route) {
                        // Grafiklerin başlangıç noktasına kadar geri giderek
                        // Çok büyük bir hedef yığını birikmesini engelle
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Aynı öğe yeniden seçildiğinde aynı hedefin birden fazla kopyasını engelle
                        launchSingleTop = true
                        // Daha önce seçilen bir öğe yeniden seçildiğinde durumu geri yükle
                        restoreState = true
                    }

                }
            )
        }
    }
}
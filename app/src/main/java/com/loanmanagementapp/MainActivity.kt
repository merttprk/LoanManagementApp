package com.loanmanagementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.loanmanagementapp.domain.repository.LoanRepository
import com.loanmanagementapp.presentation.navigation.BottomNavigationBar
import com.loanmanagementapp.presentation.navigation.NavGraph
import com.loanmanagementapp.presentation.navigation.Screen
import com.loanmanagementapp.presentation.state.AuthState
import com.loanmanagementapp.presentation.viewmodel.AuthViewModel
import com.loanmanagementapp.theme.LoanManagementAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var loanRepository: LoanRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoanManagementAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoanApp()
                }
            }
        }
    }
}

@Composable
fun LoanApp(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = remember(currentBackStackEntry) {
        currentBackStackEntry?.destination?.route ?: Screen.Login.route
    }

    val showBottomBar = remember(currentRoute, authState) {
        currentRoute != Screen.Login.route && authState is AuthState.Authenticated
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = if (authState is AuthState.Authenticated) Screen.Home.route else Screen.Login.route
        )
    }
}

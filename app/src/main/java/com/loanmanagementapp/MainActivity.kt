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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.loanmanagementapp.domain.repository.LoanRepository
import com.loanmanagementapp.presentation.navigation.BottomNavigationBar
import com.loanmanagementapp.presentation.navigation.NavGraph
import com.loanmanagementapp.presentation.ui.auth.LoginScreen
import com.loanmanagementapp.presentation.ui.dashboard.HomeScreen
import com.loanmanagementapp.theme.Blue80
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
                // A surface container using the 'background' color from the theme
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanApp() {
    var isLoggedIn by remember { mutableStateOf(true) } // Geçici olarak true yapıldı, gerçek uygulamada auth kontrolü yapılmalı
    
    if (isLoggedIn) {
        val navController = rememberNavController()
        
        Scaffold(
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { innerPadding ->
            NavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    }
}

package com.loanmanagementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var loanRepository: LoanRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           LoanApp(loanRepository)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanApp(repository: LoanRepository) {
    var isLoggedIn by remember { mutableStateOf(true) } // Geçici olarak true yapıldı, gerçek uygulamada auth kontrolü yapılmalı
    
    if (isLoggedIn) {
        val navController = rememberNavController()
        
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            NavGraph(
                navController = navController,
                repository = repository,
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    }
}

package com.loanmanagementapp

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.loanmanagementapp.data.LoanRepository
import com.loanmanagementapp.ui.screen.HomeScreen
import com.loanmanagementapp.ui.screen.LoginScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var loanRepository: LoanRepository
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           LoanApp(loanRepository)
        }
    }
}

@Composable
fun LoanApp(repository: LoanRepository) {
    var isLoggedIn by remember { mutableStateOf(false) }

    if (isLoggedIn) {
        HomeScreen(repository)
    } else {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    }
}

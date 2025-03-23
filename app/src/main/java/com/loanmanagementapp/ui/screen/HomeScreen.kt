package com.loanmanagementapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loanmanagementapp.data.Loan
import com.loanmanagementapp.data.LoanRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(repository: LoanRepository) {
    val context = LocalContext.current
    var loans by remember { mutableStateOf(emptyList<Loan>()) }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Loan Management") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "User Name:", fontSize = 16.sp)
            Button(onClick = {
                if (loans.isEmpty())
                    coroutineScope.launch {
                        loans = repository.updateLoans(context)
                    }
                else
                    loans = emptyList()
            }) {
                Text("Load Loans", fontSize = 16.sp)
            }
            loans.forEach { loan ->
                performCalculationForLoan(loan)
                Text(
                    "${loan.name} - ${loan.status}, Interest: ${loan.interestRate}%",
                    fontSize = 16.sp
                )
                performCalculation(loan)
            }
        }
    }
}

private fun performCalculation(loan: Loan) {
    repeat(1000) { _ -> Math.sqrt(loan.interestRate) }
}

private fun performCalculationForLoan(loan: Loan) {
    val result = loan.principalAmount * loan.interestRate
}
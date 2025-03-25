package com.loanmanagementapp.presentation.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.data.remote.model.Payment
import com.loanmanagementapp.presentation.components.LoanCardView
import com.loanmanagementapp.presentation.state.HomeTab
import com.loanmanagementapp.presentation.viewmodel.HomeViewModel
import com.loanmanagementapp.theme.Blue40
import com.loanmanagementapp.theme.Blue80
import com.loanmanagementapp.theme.LightBlue80
import com.loanmanagementapp.theme.White
import com.loanmanagementapp.util.StatusBarUtil
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLoanDetails: (String) -> Unit,
    onNavigateToLoanApplication: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activeLoans by viewModel.activeLoans.collectAsState()
    val passiveLoans by viewModel.passiveLoans.collectAsState()
    val paymentHistory by viewModel.paymentHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var selectedTab by remember { mutableStateOf(HomeTab.ACTIVE_LOANS) }
    
    // Load data when the screen is first displayed
    LaunchedEffect(key1 = Unit) {
        viewModel.loadActiveLoans(context)
        viewModel.loadPassiveLoans(context)
        viewModel.loadPaymentHistory(context)
        
        // Monitor error state
        viewModel.error.collect { errorMessage ->
            if (errorMessage != null) {
                Timber.e("HomeScreen veri yüklenirken hata: $errorMessage")
                // Hata durumunda kullanıcıya bildirim gösterilebilir
            }
        }
    }

    StatusBarUtil.SetStatusBarColor(color = Blue80, darkIcons = true)

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Kredi Yönetim Uygulaması") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue80,
                    titleContentColor = White
                )
            ) 
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabButton(
                    text = "Aktif Kredilerim",
                    selected = selectedTab == HomeTab.ACTIVE_LOANS,
                    onClick = { selectedTab = HomeTab.ACTIVE_LOANS }
                )
                TabButton(
                    text = "Pasif Kredilerim",
                    selected = selectedTab == HomeTab.PASSIVE_LOANS,
                    onClick = { selectedTab = HomeTab.PASSIVE_LOANS }
                )
            }

            // Content based on selected tab
            when (selectedTab) {
                HomeTab.ACTIVE_LOANS -> {
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Blue80)
                        }
                    } else if (error != null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Hata: $error",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                                Button(
                                    onClick = { 
                                        viewModel.clearError()
                                        viewModel.loadActiveLoans(context) 
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Blue80)
                                ) {
                                    Text("Tekrar Dene")
                                }
                            }
                        }
                    } else {
                        ActiveLoansTab(
                            activeLoans = activeLoans,
                            onLoanClick = onNavigateToLoanDetails
                        )
                    }
                }
                HomeTab.PASSIVE_LOANS -> {
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Blue80)
                        }
                    } else if (error != null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Hata: $error",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                                Button(
                                    onClick = { 
                                        viewModel.clearError()
                                        viewModel.loadPassiveLoans(context) 
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Blue80)
                                ) {
                                    Text("Tekrar Dene")
                                }
                            }
                        }
                    } else {
                        PassiveLoansTab(
                            passiveLoans = passiveLoans,
                            onLoanClick = { loanId ->
                                // Load payment history for this loan before navigating
                                viewModel.loadPaymentHistoryForLoan(context, loanId)
                                onNavigateToLoanDetails(loanId)
                            }
                        )
                    }
                }

                HomeTab.PAYMENT_HISTORY -> TODO()
                HomeTab.NEW_LOAN_APPLICATION -> TODO()
            }
        }
    }
}

@Composable
fun ActiveLoansTab(
    activeLoans: List<Loan>,
    onLoanClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Aktif Kredilerim",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (activeLoans.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aktif krediniz bulunmamaktadır",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(activeLoans) { loan ->
                    LoanCardView(
                        loan = loan,
                        onClick = { onLoanClick(loan.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PassiveLoansTab(
    passiveLoans: List<Loan>,
    onLoanClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Pasif Kredilerim",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (passiveLoans.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pasif krediniz bulunmamaktadır",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(passiveLoans) { loan ->
                    LoanCardView(
                        loan = loan,
                        onClick = { onLoanClick(loan.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentHistoryTab(
    paymentHistory: List<Payment>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ödeme Geçmişim",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (paymentHistory.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ödeme geçmişiniz bulunmamaktadır",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(paymentHistory) { payment ->
                    PaymentHistoryItem(payment = payment)
                }
            }
        }
    }
}

@Composable
fun PaymentHistoryItem(payment: Payment) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ödeme Tarihi:",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateFormat.format(payment.paymentDate),
                    fontWeight = FontWeight.Normal
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tutar:",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${payment.amount} TL",
                    fontWeight = FontWeight.Normal
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Durum:",
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = payment.status,
                    fontWeight = FontWeight.Normal,
                    color = if (payment.status == "Completed") Blue80 else LightBlue80
                )
            }
        }
    }
}

@Composable
fun NewLoanApplicationTab(
    onApplyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Yeni Kredi Başvurusu",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Text(
            text = "Yeni bir kredi başvurusu yapmak için aşağıdaki butona tıklayın",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Button(
            onClick = onApplyClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Kredi Başvurusu Yap",
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Blue80 else Color.LightGray.copy(alpha = 0.3f),
            contentColor = if (selected) White else Color.DarkGray
        ),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(text)
    }
}
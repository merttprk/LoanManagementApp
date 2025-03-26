package com.loanmanagementapp.presentation.ui.loans

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.data.remote.model.Payment
import com.loanmanagementapp.presentation.viewmodel.LoanDetailsViewModel
import com.loanmanagementapp.theme.Blue40
import com.loanmanagementapp.theme.Blue80
import com.loanmanagementapp.theme.White
import com.loanmanagementapp.util.StatusBarUtil
import timber.log.Timber
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.collectLatest

/**
 * Kredi detayları ekranı
 * Belirli bir kredinin detaylarını ve ödeme geçmişini gösterir
 * @param loanId Gösterilecek kredinin ID'si
 * @param onNavigateBack Geri dönüş işlevi
 * @param viewModel Kredi detayları ViewModel'i
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailsScreen(
    loanId: String,
    onNavigateBack: () -> Unit,
    viewModel: LoanDetailsViewModel = hiltViewModel()
) {
    StatusBarUtil.SetStatusBarColor(color = Blue80, darkIcons = true)
    
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val paymentState by viewModel.paymentState.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Kredi Detayları", "Ödeme Geçmişi")
    
    // Kredileri ve ödemeleri yükle
    LaunchedEffect(key1 = loanId) {
        viewModel.getLoanById(context, loanId)
        viewModel.getLoanPayments(context, loanId)
        
        // Hata durumunu izle
        viewModel.state.collectLatest { state ->
            if (state.error != null) {
                // Hata durumunda loglama veya kullanıcıya bildirim gösterme işlemleri yapılabilir
                Timber.e("Kredi detayları yüklenirken hata: ${state.error}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Kredi Detayları",
                        color = White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue80
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Blue40,
                contentColor = White
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Blue80)
                }
            } else if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hata: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (state.selectedLoan == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Kredi bulunamadı",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                when (selectedTabIndex) {
                    0 -> LoanDetailsTab(state.selectedLoan!!)
                    1 -> PaymentHistoryTab(
                        payments = paymentState.payments,
                        isLoading = paymentState.isLoading,
                        error = paymentState.error
                    )
                }
            }
        }
    }
}

/**
 * Kredi detayları tab içeriği
 * @param loan Gösterilecek kredi
 */
@Composable
private fun LoanDetailsTab(loan: Loan) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = loan.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Durum: ${getStatusText(loan.status)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Ana Para: ${currencyFormat.format(loan.principalAmount)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Faiz Oranı: %${loan.interestRate}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Vade: ${loan.dueIn / 30} ay",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

/**
 * Ödeme geçmişi tab içeriği
 * @param payments Ödeme listesi
 * @param isLoading Yükleniyor durumu
 * @param error Hata mesajı
 */
@Composable
private fun PaymentHistoryTab(
    payments: List<Payment>,
    isLoading: Boolean,
    error: String?
) {
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
            Text(
                text = "Hata: $error",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else if (payments.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ödeme geçmişi bulunamadı",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(payments) { payment ->
                PaymentItem(payment)
                Divider()
            }
        }
    }
}

/**
 * Ödeme öğesi
 * @param payment Gösterilecek ödeme
 */
@Composable
private fun PaymentItem(payment: Payment) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR"))
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Ödeme Tarihi: ${dateFormat.format(Date(payment.paymentDate))}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Tutar: ${currencyFormat.format(payment.amount)}",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Açıklama: ${payment.description ?: "Açıklama bulunamadı"}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Kredi durumunu Türkçe metne çevirir
 * @param status Kredi durumu
 * @return Türkçe durum metni
 */
private fun getStatusText(status: String): String {
    return when (status.lowercase()) {
        "active" -> "Aktif"
        "paid" -> "Ödenmiş"
        "overdue" -> "Gecikmiş"
        "default" -> "Temerrüt"
        else -> status
    }
}

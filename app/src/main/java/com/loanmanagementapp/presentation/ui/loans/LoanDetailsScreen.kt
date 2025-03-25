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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.presentation.components.LoanDetailCard
import com.loanmanagementapp.presentation.components.ReusableListItem
import com.loanmanagementapp.presentation.viewmodel.LoanDetailsViewModel
import com.loanmanagementapp.theme.Blue40
import com.loanmanagementapp.theme.Blue80
import com.loanmanagementapp.theme.White
import com.loanmanagementapp.util.StatusBarUtil

/**
 * Kredi detayları ekranı
 * Kredileri listeler ve seçilen kredinin detaylarını gösterir
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanDetailsScreen(
    viewModel: LoanDetailsViewModel = hiltViewModel()
) {
    // Status bar'ı beyaz yap ve ikonları koyu renk olarak ayarla
    StatusBarUtil.SetStatusBarColor(color = White, darkIcons = true)
    
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    
    // Seçilen kredi ve vade için state'ler
    var selectedLoan by remember { mutableStateOf<Loan?>(null) }
    var selectedTerm by remember { mutableStateOf(12) } // Varsayılan 12 ay
    var isDropdownExpanded by remember { mutableStateOf(false) }
    
    // Hesaplanan faiz
    val calculatedInterest = selectedLoan?.let { 
        viewModel.calculateInterest(it, selectedTerm) 
    } ?: 0.0
    
    // Kredileri yükle
    LaunchedEffect(key1 = true) {
        viewModel.getLoans(context)
    }
    
    // Kredi seçildiğinde viewModel'i güncelle
    LaunchedEffect(key1 = selectedLoan) {
        selectedLoan?.let { viewModel.selectLoan(it) }
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
            } else if (state.loans.isEmpty()) {
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Kredi seçim dropdown'ı
                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedLoan?.name ?: "Kredi seçiniz",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            label = { Text("Kredi") },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Blue80,
                                unfocusedBorderColor = Blue40.copy(alpha = 0.7f),
                                focusedLabelColor = Blue80,
                                unfocusedLabelColor = Blue40.copy(alpha = 0.7f),
                                cursorColor = Blue80
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            state.loans.forEach { loan ->
                                DropdownMenuItem(
                                    text = { Text(loan.name) },
                                    onClick = {
                                        selectedLoan = loan
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Vade seçimi
                    val termOptions = listOf(6, 12, 24, 36, 48, 60, 120, 240, 360)
                    var isTermDropdownExpanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = isTermDropdownExpanded,
                        onExpandedChange = { isTermDropdownExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = "$selectedTerm ay",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTermDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            label = { Text("Vade") },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Blue80,
                                unfocusedBorderColor = Blue40.copy(alpha = 0.7f),
                                focusedLabelColor = Blue80,
                                unfocusedLabelColor = Blue40.copy(alpha = 0.7f),
                                cursorColor = Blue80
                            )
                        )
                        
                        ExposedDropdownMenu(
                            expanded = isTermDropdownExpanded,
                            onDismissRequest = { isTermDropdownExpanded = false }
                        ) {
                            termOptions.forEach { term ->
                                DropdownMenuItem(
                                    text = { Text("$term ay") },
                                    onClick = {
                                        selectedTerm = term
                                        isTermDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Kredi detay kartı
                    if (selectedLoan != null) {
                        LoanDetailCard(
                            loan = selectedLoan!!,
                            calculatedInterest = calculatedInterest,
                            term = selectedTerm,
                            containerColor = Blue80.copy(alpha = 0.1f),
                            contentColor = Blue80
                        )
                    } else {
                        Text(
                            text = "Detayları görmek için bir kredi seçin",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Tüm krediler başlığı
                    Text(
                        text = "Tüm Krediler",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Divider()
                    
                    // Kredi listesi
                    LazyColumn {
                        items(state.loans) { loan ->
                            LoanListItem(
                                loan = loan,
                                isSelected = selectedLoan?.id == loan.id,
                                onClick = { selectedLoan = loan }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

/**
 * Kredi listesi öğesi
 * @param loan Gösterilecek kredi
 * @param isSelected Seçili olup olmadığı
 * @param onClick Tıklama işlevi
 */
@Composable
fun LoanListItem(
    loan: Loan,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Blue80.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    
    ReusableListItem(
        primaryText = loan.name,
        secondaryText = "Ana Para: ${loan.principalAmount} TL • Faiz: %${loan.interestRate}",
        trailingText = getStatusText(loan.status),
        onClick = onClick,
        backgroundColor = backgroundColor,
        contentColor = if (isSelected) Blue80 else MaterialTheme.colorScheme.onSurface
    )
}

/**
 * Kredi durumunu Türkçe metne çevirir
 * @param status Kredi durumu
 * @return Türkçe durum metni
 */
fun getStatusText(status: String): String {
    return when (status.lowercase()) {
        "active" -> "Aktif"
        "paid" -> "Ödenmiş"
        "overdue" -> "Gecikmiş"
        "default" -> "Temerrüt"
        else -> status
    }
}

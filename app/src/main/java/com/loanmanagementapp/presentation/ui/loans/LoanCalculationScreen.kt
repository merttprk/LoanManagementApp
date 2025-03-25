package com.loanmanagementapp.presentation.ui.loans

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.presentation.viewmodel.LoanCalculationViewModel
import com.loanmanagementapp.theme.Blue40
import com.loanmanagementapp.theme.Blue80
import com.loanmanagementapp.theme.White
import com.loanmanagementapp.util.StatusBarUtil
import java.text.NumberFormat
import java.util.*

/**
 * Kredi hesaplama ekranı
 * Kullanıcıların farklı kredi türleri için faiz hesaplaması yapmasını sağlar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanCalculationScreen(
    viewModel: LoanCalculationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    // Status bar'ı beyaz yap ve ikonları koyu renk olarak ayarla
    StatusBarUtil.SetStatusBarColor(color = Blue80, darkIcons = true)
    
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Kredi Hesaplama",
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
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Kredi türü seçimi
            LoanTypeSelector(
                selectedLoanType = state.selectedLoanType,
                onLoanTypeSelected = viewModel::onLoanTypeSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Kredi miktarı girişi
            OutlinedTextField(
                value = state.loanAmount,
                onValueChange = viewModel::onLoanAmountChanged,
                label = { Text("Kredi Miktarı (TL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Blue80,
                    unfocusedBorderColor = Blue40.copy(alpha = 0.7f),
                    focusedLabelColor = Blue80,
                    unfocusedLabelColor = Blue40.copy(alpha = 0.7f),
                    cursorColor = Blue80
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Vade süresi girişi ve önerilen vade butonu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.loanTerm,
                    onValueChange = viewModel::onLoanTermChanged,
                    label = { Text("Vade (Ay)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue80,
                        unfocusedBorderColor = Blue40.copy(alpha = 0.7f),
                        focusedLabelColor = Blue80,
                        unfocusedLabelColor = Blue40.copy(alpha = 0.7f),
                        cursorColor = Blue80
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = viewModel::useRecommendedTerm,
                    modifier = Modifier.padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue80
                    )
                ) {
                    Text("Önerilen Vade")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Faiz oranı girişi
            OutlinedTextField(
                value = state.interestRate,
                onValueChange = viewModel::onInterestRateChanged,
                label = { Text("Faiz Oranı (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Blue80,
                    unfocusedBorderColor = Blue40.copy(alpha = 0.7f),
                    focusedLabelColor = Blue80,
                    unfocusedLabelColor = Blue40.copy(alpha = 0.7f),
                    cursorColor = Blue80
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Hesaplama ve sıfırlama butonları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = viewModel::calculateLoanInterest,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue80
                    )
                ) {
                    Text("Hesapla")
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                OutlinedButton(
                    onClick = viewModel::resetCalculation,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Blue80
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(Blue80)
                    )
                ) {
                    Text("Sıfırla")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Hesaplama sonuçları
            if (state.calculatedInterest != null && state.totalPayment != null && state.monthlyPayment != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Blue80.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Hesaplama Sonuçları",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Blue80
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        ResultRow(label = "Kredi Türü", value = state.selectedLoanType.name)
                        ResultRow(label = "Kredi Miktarı", value = formatCurrency(state.loanAmount.toDoubleOrNull() ?: 0.0))
                        ResultRow(label = "Vade", value = "${state.loanTerm} Ay")
                        ResultRow(label = "Faiz Oranı", value = "%${state.interestRate}")
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        ResultRow(label = "Toplam Faiz", value = formatCurrency(state.calculatedInterest!!))
                        ResultRow(label = "Toplam Ödeme", value = formatCurrency(state.totalPayment!!))
                        ResultRow(
                            label = "Aylık Ödeme", 
                            value = formatCurrency(state.monthlyPayment!!),
                            isHighlighted = true
                        )
                    }
                }
            }

            // Hata mesajı
            state.errorMessage?.let { errorMessage ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            // Yükleniyor göstergesi
            if (state.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = Blue80)
            }
        }
    }
}

/**
 * Kredi türü seçim komponenti
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanTypeSelector(
    selectedLoanType: LoanType,
    onLoanTypeSelected: (LoanType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = getLoanTypeDisplayName(selectedLoanType),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = { Text("Kredi Türü") },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = Blue80,
                unfocusedBorderColor = Blue40.copy(alpha = 0.7f),
                focusedLabelColor = Blue80,
                unfocusedLabelColor = Blue40.copy(alpha = 0.7f),
                cursorColor = Blue80
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            LoanType.values().forEach { loanType ->
                DropdownMenuItem(
                    text = { Text(getLoanTypeDisplayName(loanType)) },
                    onClick = {
                        onLoanTypeSelected(loanType)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Sonuç satırı komponenti
 */
@Composable
fun ResultRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    val textStyle = if (isHighlighted) {
        MaterialTheme.typography.bodyLarge.copy(color = Blue80)
    } else {
        MaterialTheme.typography.bodyMedium
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = textStyle)
        Text(text = value, style = textStyle)
    }
}

/**
 * Kredi türü için görüntülenecek adı döndürür
 */
fun getLoanTypeDisplayName(loanType: LoanType): String {
    return when (loanType) {
        LoanType.PERSONAL -> "Bireysel Kredi"
        LoanType.AUTO -> "Taşıt Kredisi"
        LoanType.MORTGAGE -> "Konut Kredisi"
        LoanType.BUSINESS -> "İşletme Kredisi"
        LoanType.EDUCATION -> "Eğitim Kredisi"
    }
}

/**
 * Para birimini formatlar
 */
fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    return format.format(amount)
}

package com.loanmanagementapp.presentation.ui.loans

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.presentation.viewmodel.LoanCalculationViewModel
import java.text.NumberFormat
import java.util.*

/**
 * Kredi hesaplama ekranı
 * Kullanıcıların farklı kredi türleri için faiz hesaplaması yapmasını sağlar
 */
@Composable
fun LoanCalculationScreen(
    viewModel: LoanCalculationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Başlık
        Text(
            text = "Kredi Hesaplama",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

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
            modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = viewModel::useRecommendedTerm,
                modifier = Modifier.padding(top = 8.dp)
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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Hesaplama ve sıfırlama butonları
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = viewModel::calculateLoanInterest,
                modifier = Modifier.weight(1f)
            ) {
                Text("Hesapla")
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            OutlinedButton(
                onClick = viewModel::resetCalculation,
                modifier = Modifier.weight(1f)
            ) {
                Text("Sıfırla")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Hesaplama sonuçları
        if (state.calculatedInterest != null && state.totalPayment != null && state.monthlyPayment != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        textAlign = TextAlign.Center
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
            CircularProgressIndicator()
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
            label = { Text("Kredi Türü") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isHighlighted) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = if (isHighlighted) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
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
    }
}

/**
 * Para birimini formatlar
 */
fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    return format.format(amount)
}

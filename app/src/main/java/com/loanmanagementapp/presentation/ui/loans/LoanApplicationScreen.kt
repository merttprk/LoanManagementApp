package com.loanmanagementapp.presentation.ui.loans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.presentation.viewmodel.LoanApplicationViewModel
import com.loanmanagementapp.theme.Blue40
import com.loanmanagementapp.theme.Blue80
import com.loanmanagementapp.theme.White
import com.loanmanagementapp.util.StatusBarUtil
import kotlinx.coroutines.launch

/**
 * Kredi başvuru ekranı
 * @param onNavigateBack Geri dönüş işlevi
 * @param onApplicationSubmitted Başvuru tamamlandığında çağrılacak işlev
 * @param viewModel Kredi başvuru ViewModel'i
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanApplicationScreen(
    onNavigateBack: () -> Unit,
    onApplicationSubmitted: () -> Unit,
    viewModel: LoanApplicationViewModel = hiltViewModel()
) {
    // Status bar'ı beyaz yap ve ikonları koyu renk olarak ayarla
    StatusBarUtil.SetStatusBarColor(color = Blue80, darkIcons = true)
    
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    // Form state'leri
    var loanName by remember { mutableStateOf("") }
    var principalAmount by remember { mutableStateOf("") }
    var selectedLoanType by remember { mutableStateOf(LoanType.PERSONAL) }
    var isLoanTypeDropdownExpanded by remember { mutableStateOf(false) }
    
    // Form doğrulama hataları
    var loanNameError by remember { mutableStateOf<String?>(null) }
    var principalAmountError by remember { mutableStateOf<String?>(null) }
    
    // Başvuru başarılı olduğunda ana sayfaya dön
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Kredi başvurunuz başarıyla alındı")
                viewModel.resetSuccess()
                onApplicationSubmitted()
            }
        }
    }
    
    // Hata durumunda snackbar göster
    LaunchedEffect(error) {
        error?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Yeni Kredi Başvurusu",
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Kredi türü seçimi
                ExposedDropdownMenuBox(
                    expanded = isLoanTypeDropdownExpanded,
                    onExpandedChange = { isLoanTypeDropdownExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = getLoanTypeText(selectedLoanType),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isLoanTypeDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text("Kredi Türü") }
                    )
                    
                    ExposedDropdownMenu(
                        expanded = isLoanTypeDropdownExpanded,
                        onDismissRequest = { isLoanTypeDropdownExpanded = false }
                    ) {
                        LoanType.entries.forEach { loanType ->
                            DropdownMenuItem(
                                text = { Text(getLoanTypeText(loanType)) },
                                onClick = {
                                    selectedLoanType = loanType
                                    isLoanTypeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Kredi adı
                OutlinedTextField(
                    value = loanName,
                    onValueChange = { 
                        loanName = it
                        loanNameError = if (it.isBlank()) "Kredi adı boş olamaz" else null
                    },
                    label = { Text("Kredi Adı") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = loanNameError != null,
                    supportingText = { loanNameError?.let { Text(it) } }
                )
                
                // Ana para miktarı
                OutlinedTextField(
                    value = principalAmount,
                    onValueChange = { 
                        principalAmount = it
                        principalAmountError = when {
                            it.isBlank() -> "Ana para miktarı boş olamaz"
                            it.toDoubleOrNull() == null -> "Geçerli bir sayı giriniz"
                            it.toDouble() <= 0 -> "Ana para miktarı sıfırdan büyük olmalıdır"
                            else -> null
                        }
                    },
                    label = { Text("Ana Para Miktarı (TL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = principalAmountError != null,
                    supportingText = { principalAmountError?.let { Text(it) } }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Başvuru butonu
                Button(
                    onClick = {
                        // Form doğrulama
                        loanNameError = if (loanName.isBlank()) "Kredi adı boş olamaz" else null
                        principalAmountError = when {
                            principalAmount.isBlank() -> "Ana para miktarı boş olamaz"
                            principalAmount.toDoubleOrNull() == null -> "Geçerli bir sayı giriniz"
                            principalAmount.toDouble() <= 0 -> "Ana para miktarı sıfırdan büyük olmalıdır"
                            else -> null
                        }
                        
                        // Eğer form geçerliyse başvuruyu gönder
                        if (loanNameError == null && principalAmountError == null) {
                            viewModel.applyForLoan(
                                context,
                                loanName,
                                principalAmount.toDouble(),
                                selectedLoanType
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Başvuru Yap")
                }
            }
            
            // Yükleniyor göstergesi
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

/**
 * Kredi türünü Türkçe metne çevirir
 * @param loanType Kredi türü
 * @return Türkçe kredi türü metni
 */
private fun getLoanTypeText(loanType: LoanType): String {
    return when (loanType) {
        LoanType.PERSONAL -> "Bireysel Kredi"
        LoanType.AUTO -> "Taşıt Kredisi"
        LoanType.MORTGAGE -> "Konut Kredisi"
        LoanType.BUSINESS -> "İşletme Kredisi"
        LoanType.EDUCATION -> "Eğitim Kredisi"
    }
}

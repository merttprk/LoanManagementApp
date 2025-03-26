package com.loanmanagementapp.presentation.ui.loans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.core.type.LoanTypeText.Companion.getLoanTypeText
import com.loanmanagementapp.presentation.viewmodel.LoanApplicationViewModel
import com.loanmanagementapp.theme.Blue80
import com.loanmanagementapp.theme.White
import com.loanmanagementapp.util.StatusBarUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanApplicationScreen(
    onNavigateBack: () -> Unit,
    onApplicationSubmitted: () -> Unit,
    viewModel: LoanApplicationViewModel = hiltViewModel()
) {
    StatusBarUtil.SetStatusBarColor(color = Blue80, darkIcons = true)

    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var loanName by remember { mutableStateOf("") }
    var principalAmount by remember { mutableStateOf("") }
    var selectedLoanType by remember { mutableStateOf(LoanType.PERSONAL) }
    var isLoanTypeDropdownExpanded by remember { mutableStateOf(false) }
    var isTermDropdownExpanded by remember { mutableStateOf(false) }
    var selectedTerm by remember { mutableIntStateOf(12) }
    val termOptions = listOf(3, 6, 12, 24, 36, 48, 60, 120, 240, 360)

    var loanNameError by remember { mutableStateOf<String?>(null) }
    var principalAmountError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Kredi başvurunuz başarıyla alındı")
                viewModel.resetSuccess()
                onApplicationSubmitted()
            }
        }
    }

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
            ) {
                // Kredi türü seçimi
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Kredi Türü",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
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
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
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
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // Kredi adı
                    OutlinedTextField(
                        value = loanName,
                        onValueChange = {
                            loanName = it
                            loanNameError = if (it.isBlank()) "Kredi adı boş olamaz" else null
                        },
                        label = { Text("Kredi Adı") },
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
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
                }

                // Vade seçimi
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Vade",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    ExposedDropdownMenuBox(
                        expanded = isTermDropdownExpanded,
                        onExpandedChange = { isTermDropdownExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = "$selectedTerm Ay",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTermDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )

                        ExposedDropdownMenu(
                            expanded = isTermDropdownExpanded,
                            onDismissRequest = { isTermDropdownExpanded = false }
                        ) {
                            termOptions.forEach { term ->
                                DropdownMenuItem(
                                    text = { Text("$term Ay") },
                                    onClick = {
                                        selectedTerm = term
                                        isTermDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Başvuru butonu
                Button(
                    onClick = {
                        loanNameError = if (loanName.isBlank()) "Kredi adı boş olamaz" else null
                        principalAmountError = when {
                            principalAmount.isBlank() -> "Ana para miktarı boş olamaz"
                            principalAmount.toDoubleOrNull() == null -> "Geçerli bir sayı giriniz"
                            principalAmount.toDouble() <= 0 -> "Ana para miktarı sıfırdan büyük olmalıdır"
                            else -> null
                        }

                        if (loanNameError == null && principalAmountError == null) {
                            viewModel.applyForLoan(
                                context,
                                loanName,
                                principalAmount.toDouble(),
                                selectedLoanType,
                                selectedTerm
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp).padding(top = 16.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Başvuru Yap")
                    }
                }
            }

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
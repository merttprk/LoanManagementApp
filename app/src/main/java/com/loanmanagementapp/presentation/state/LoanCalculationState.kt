package com.loanmanagementapp.presentation.state

import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan

/**
 * Kredi hesaplama ekranı için UI durum sınıfı
 * Ekranda gösterilecek verileri ve yükleme durumunu içerir
 */
data class LoanCalculationState(
    // Yükleme durumu
    val isLoading: Boolean = false,
    // Hata mesajı (varsa)
    val errorMessage: String? = null,
    
    // Kredi hesaplama girdileri
    val selectedLoanType: LoanType = LoanType.PERSONAL,
    val loanAmount: String = "",
    val loanTerm: String = "",
    val interestRate: String = "",
    
    // Hesaplama sonuçları
    val calculatedInterest: Double? = null,
    val totalPayment: Double? = null,
    val monthlyPayment: Double? = null,
    
    // Önerilen vade süresi (ay cinsinden)
    val recommendedTerm: Int? = null,
    
    // Mevcut kredi listesi
    val loans: List<Loan> = emptyList()
)

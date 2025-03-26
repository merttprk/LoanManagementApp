package com.loanmanagementapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.domain.usecase.loan.ApplyForLoanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Kredi başvuru ekranı için ViewModel sınıfı
 */
@HiltViewModel
class LoanApplicationViewModel @Inject constructor(
    private val applyForLoanUseCase: ApplyForLoanUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    /**
     * Yeni kredi başvurusu yapar
     * @param context Uygulama context'i
     * @param loanName Kredi adı
     * @param principalAmount Ana para miktarı
     * @param loanType Kredi türü
     * @param termInMonths Vade süresi (ay cinsinden)
     */
    fun applyForLoan(
        context: Context,
        loanName: String,
        principalAmount: Double,
        loanType: LoanType,
        termInMonths: Int = 0
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                // Kredi başvurusu yap
                val loan = applyForLoanUseCase(
                    context,
                    loanName,
                    principalAmount,
                    loanType,
                    termInMonths
                )
                
                // Başarılı sonuç
                _isSuccess.value = true
            } catch (e: Exception) {
                // Hata durumu
                _error.value = "Kredi başvurusu yapılırken bir hata oluştu: ${e.message}"
                _isSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Hata mesajını temizler
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Başarı durumunu sıfırlar
     */
    fun resetSuccess() {
        _isSuccess.value = false
    }
}

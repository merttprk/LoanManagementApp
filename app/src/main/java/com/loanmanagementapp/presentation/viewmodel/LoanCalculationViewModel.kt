package com.loanmanagementapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.domain.usecase.loan.CalculateInterestUseCase
import com.loanmanagementapp.presentation.state.LoanCalculationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * Kredi hesaplama ekranı için ViewModel
 * Kredi hesaplama işlemlerini yönetir ve UI durumunu günceller
 */
@HiltViewModel
class LoanCalculationViewModel @Inject constructor(
    private val calculateInterestUseCase: CalculateInterestUseCase,
) : ViewModel() {

    // UI durumu için MutableStateFlow
    private val _state = MutableStateFlow(LoanCalculationState())
    val state: StateFlow<LoanCalculationState> = _state.asStateFlow()

    init {
        // Başlangıçta seçili kredi türü için önerilen vade süresini al
        updateRecommendedTerm(LoanType.PERSONAL)
    }

    /**
     * Kredi türünü güncelle ve önerilen vade süresini al
     * @param loanType Seçilen kredi türü
     */
    fun onLoanTypeSelected(loanType: LoanType) {
        _state.update { it.copy(selectedLoanType = loanType) }
        updateRecommendedTerm(loanType)
    }

    /**
     * Kredi miktarını güncelle
     * @param amount Kredi miktarı
     */
    fun onLoanAmountChanged(amount: String) {
        _state.update { it.copy(loanAmount = amount) }
    }

    /**
     * Vade süresini güncelle
     * @param term Vade süresi (ay cinsinden)
     */
    fun onLoanTermChanged(term: String) {
        _state.update { it.copy(loanTerm = term) }
    }

    /**
     * Faiz oranını güncelle
     * @param rate Faiz oranı
     */
    fun onInterestRateChanged(rate: String) {
        _state.update { it.copy(interestRate = rate) }
    }

    /**
     * Seçili kredi türü için önerilen vade süresini güncelle
     * @param loanType Kredi türü
     */
    private fun updateRecommendedTerm(loanType: LoanType) {
        val recommendedTerm = calculateInterestUseCase.getRecommendedTerm(loanType)
        _state.update { it.copy(
            recommendedTerm = recommendedTerm,
            loanTerm = recommendedTerm.toString()
        ) }
    }

    /**
     * Kredi faizini hesapla
     */
    fun calculateLoanInterest() {
        val currentState = _state.value
        
        // Giriş değerlerini kontrol et
        val loanAmount = currentState.loanAmount.toDoubleOrNull()
        val loanTerm = currentState.loanTerm.toIntOrNull()
        val interestRate = currentState.interestRate.toDoubleOrNull()
        
        if (loanAmount == null || loanTerm == null || interestRate == null) {
            _state.update { it.copy(errorMessage = "Lütfen tüm alanları doğru formatta doldurun") }
            return
        }
        
        // Hata mesajını temizle
        _state.update { it.copy(errorMessage = null, isLoading = true) }
        
        viewModelScope.launch {
            try {
                // Hesaplama için kredi nesnesi oluştur
                val loan = Loan(
                    id = UUID.randomUUID().toString(),
                    name = "Calculation ${currentState.selectedLoanType}",
                    principalAmount = loanAmount,
                    interestRate = interestRate,
                    dueIn = loanTerm,
                    status = "active"
                )
                
                // Faizi hesapla
                val interest = calculateInterestUseCase.execute(
                    loan = loan,
                    months = loanTerm,
                    loanType = currentState.selectedLoanType
                )
                
                // Toplam ödeme ve aylık ödeme hesapla
                val totalPayment = loanAmount + interest
                val monthlyPayment = totalPayment / loanTerm
                
                // Durumu güncelle
                _state.update { it.copy(
                    isLoading = false,
                    calculatedInterest = interest,
                    totalPayment = totalPayment,
                    monthlyPayment = monthlyPayment
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    errorMessage = "Hesaplama sırasında hata oluştu: ${e.message}"
                ) }
            }
        }
    }

    /**
     * Önerilen vade süresini kullan
     */
    fun useRecommendedTerm() {
        _state.value.recommendedTerm?.let { recommendedTerm ->
            _state.update { it.copy(loanTerm = recommendedTerm.toString()) }
        }
    }

    /**
     * Hesaplama formunu sıfırla
     */
    fun resetCalculation() {
        val currentLoanType = _state.value.selectedLoanType
        _state.update { 
            LoanCalculationState(selectedLoanType = currentLoanType)
        }
        updateRecommendedTerm(currentLoanType)
    }
}

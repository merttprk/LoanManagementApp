package com.loanmanagementapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.domain.usecase.loan.GetLoanDetailsUseCase
import com.loanmanagementapp.presentation.state.LoanDetailsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Kredi detayları ekranı için ViewModel
 * Kredi detaylarını getirme ve işleme mantığını içerir
 */
@HiltViewModel
class LoanDetailsViewModel @Inject constructor(
    private val getLoanDetailsUseCase: GetLoanDetailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoanDetailsState())
    val state: StateFlow<LoanDetailsState> = _state.asStateFlow()

    /**
     * Tüm kredileri getirir
     * @param context Uygulama context'i
     */
    fun getLoans(context: Context) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val loans = getLoanDetailsUseCase(context)
                _state.update { it.copy(isLoading = false, loans = loans) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Seçilen krediyi günceller
     * @param loan Seçilen kredi
     */
    fun selectLoan(loan: Loan) {
        _state.update { it.copy(selectedLoan = loan) }
    }

    /**
     * Belirli bir kredi için faiz hesaplar
     * @param loan Faiz hesaplanacak kredi
     * @param months Faiz hesaplaması için ay sayısı
     * @return Hesaplanan faiz tutarı
     */
    fun calculateInterest(loan: Loan, months: Int): Double {
        return getLoanDetailsUseCase.calculateInterestForLoan(loan, months)
    }
}

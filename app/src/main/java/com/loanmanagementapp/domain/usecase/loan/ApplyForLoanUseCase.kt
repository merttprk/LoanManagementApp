package com.loanmanagementapp.domain.usecase.loan

import android.content.Context
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.domain.repository.LoanRepository
import java.util.UUID
import javax.inject.Inject

/**
 * Yeni kredi başvurusu yapan use case
 */
class ApplyForLoanUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    /**
     * Yeni bir kredi başvurusu yapar
     * @param context Uygulama context'i
     * @param loanName Kredi adı
     * @param principalAmount Ana para miktarı
     * @param loanType Kredi türü
     * @return Oluşturulan kredi nesnesi
     */
    suspend operator fun invoke(
        context: Context,
        loanName: String,
        principalAmount: Double,
        loanType: LoanType
    ): Loan {
        // Kredi türüne göre önerilen vade süresini al
        val recommendedTerm = loanRepository.getRecommendedTerm(loanType)
        
        // Kredi türüne göre faiz oranını belirle
        val interestRate = when (loanType) {
            LoanType.PERSONAL -> 12.5
            LoanType.AUTO -> 7.5
            LoanType.MORTGAGE -> 5.25
            LoanType.BUSINESS -> 9.75
            LoanType.EDUCATION -> 10.0
        }
        
        // Yeni kredi oluştur
        val newLoan = Loan(
            id = UUID.randomUUID().toString(),
            name = loanName,
            principalAmount = principalAmount,
            interestRate = interestRate,
            status = "Active",
            dueIn = recommendedTerm * 30 // Ay cinsinden vadeyi gün cinsine çevir
        )
        
        // Mevcut kredileri getir
        val currentLoans = loanRepository.updateLoans(context).toMutableList()
        
        // Yeni krediyi ekle
        currentLoans.add(newLoan)
        
        // Güncellenmiş listeyi kaydet
        loanRepository.saveLoans(context, currentLoans)
        
        return newLoan
    }
}

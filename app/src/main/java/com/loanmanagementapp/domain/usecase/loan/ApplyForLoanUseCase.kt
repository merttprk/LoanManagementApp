package com.loanmanagementapp.domain.usecase.loan

import android.content.Context
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.domain.repository.LoanRepository
import java.util.Random
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
     * @param termInMonths Vade süresi (ay cinsinden)
     * @return Oluşturulan kredi nesnesi
     */
    suspend operator fun invoke(
        context: Context,
        loanName: String,
        principalAmount: Double,
        loanType: LoanType,
        termInMonths: Int = 0
    ): Loan {
        // Kredi türüne göre önerilen vade süresini al (eğer kullanıcı bir vade seçmediyse)
        val finalTerm = if (termInMonths > 0) {
            termInMonths
        } else {
            loanRepository.getRecommendedTerm(loanType)
        }
        
        // Kredi türüne göre faiz oranını belirle
        val interestRate = when (loanType) {
            LoanType.PERSONAL -> 12.5
            LoanType.AUTO -> 7.5
            LoanType.MORTGAGE -> 5.25
            LoanType.BUSINESS -> 9.75
            LoanType.EDUCATION -> 10.0
        }
        
        // 4 haneli rastgele bir sayı oluştur
        val random = Random()
        val randomNumber = 1000 + random.nextInt(9000) // 1000-9999 arası
        
        // Yeni kredi oluştur
        val newLoan = Loan(
            id = "L$randomNumber",
            name = loanName,
            principalAmount = principalAmount,
            interestRate = interestRate,
            status = "active",
            dueIn = finalTerm * 30, // Ay cinsinden vadeyi gün cinsine çevir
            type = loanType
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

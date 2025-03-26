package com.loanmanagementapp.domain.usecase.loan

import android.content.Context
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.data.remote.model.Payment
import com.loanmanagementapp.domain.repository.LoanRepository
import com.loanmanagementapp.util.DateUtil
import java.util.Random
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.pow

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
        val loanId = "L$randomNumber"
        
        // Aylık ödeme tutarını hesapla
        val monthlyInterestRate = interestRate / 12 / 100 // Aylık faiz oranı
        val monthlyPayment = calculateMonthlyPayment(principalAmount, monthlyInterestRate, finalTerm)
        
        // Yeni kredi oluştur
        val newLoan = Loan(
            id = loanId,
            name = loanName,
            principalAmount = principalAmount,
            interestRate = interestRate,
            status = "active",
            dueIn = finalTerm * 30, // Ay cinsinden vadeyi gün cinsine çevir
            type = loanType
        )
        
        // Ödeme planını oluştur
        val payments = createPaymentSchedule(loanId, monthlyPayment, finalTerm)
        
        // Mevcut kredileri getir ve yeni krediyi ekle
        val currentLoans = loanRepository.updateLoans(context).toMutableList()
        currentLoans.add(newLoan)
        
        // Güncellenmiş kredi listesini kaydet
        loanRepository.saveLoans(context, currentLoans)
        
        // Ödeme planını Firestore'a kaydet
        loanRepository.savePayments(loanId, payments)
        
        return newLoan
    }
    
    /**
     * Aylık ödeme tutarını hesaplar
     */
    private fun calculateMonthlyPayment(principal: Double, monthlyRate: Double, termInMonths: Int): Double {
        return principal * monthlyRate * (1 + monthlyRate).pow(termInMonths) / 
               ((1 + monthlyRate).pow(termInMonths) - 1)
    }
    
    /**
     * Ödeme planını oluşturur
     */
    private fun createPaymentSchedule(loanId: String, monthlyPayment: Double, termInMonths: Int): List<Payment> {
        val payments = mutableListOf<Payment>()
        val calendar = Calendar.getInstance()
        
        // İlk ödeme tarihi bir ay sonrası
        calendar.add(Calendar.MONTH, 1)
        
        for (month in 1..termInMonths) {
            val payment = Payment(
                id = "P${loanId}_$month",
                loanId = loanId,
                amount = monthlyPayment,
                dueDate = calendar.time,
                status = "pending"
            )
            payments.add(payment)
            
            // Sonraki ay
            calendar.add(Calendar.MONTH, 1)
        }
        
        return payments
    }
}

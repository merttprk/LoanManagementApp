package com.loanmanagementapp.core.type

enum class LoanTypeText(val description: String) {
    PERSONAL("Bireysel Kredi"),
    AUTO("Taşıt Kredisi"),
    MORTGAGE("Konut Kredisi"),
    BUSINESS("İşletme Kredisi"),
    EDUCATION("Eğitim Kredisi");

    companion object {
        fun getLoanTypeText(loanType: LoanType): String {
            return entries.find { it.name == loanType.name }?.description ?: ""
        }
    }
}

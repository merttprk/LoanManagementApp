package com.loanmanagementapp.data.remote.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val investorLevel: String,
    val createdAt: Long = 0 // Hesap oluşturma tarihi
)
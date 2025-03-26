package com.loanmanagementapp.util

import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("tr"))

    fun parseDate(dateObj: Any?): Long {
        return when (dateObj) {
            is String -> try {
                dateFormat.parse(dateObj)?.time ?: System.currentTimeMillis()
            } catch (e: Exception) {
                Timber.e(e, "Tarih ayrıştırılamadı: $dateObj")
                System.currentTimeMillis()
            }
            is Date -> dateObj.time
            is Long -> dateObj
            else -> System.currentTimeMillis()
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp * 1000) // Saniyeyi milisaniyeye çevir
        return dateFormat.format(date)
    }
}
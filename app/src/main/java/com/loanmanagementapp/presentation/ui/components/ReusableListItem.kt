package com.loanmanagementapp.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReusableListItem(
    date: Date,
    amount: Double,
    status: String,
    paymentDate: Date? = null,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("tr"))
    
    val cardColor = when (status.lowercase()) {
        "bekliyor" -> Color(0xFFFFA500) // Turuncu
        "gecikmiş" -> Color(0xFFFF0000) // Kırmızı
        "tamamlandı" -> Color(0xFF4CAF50) // Yeşil
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Ödeme Tarihi: ${dateFormat.format(date)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tutar: ₺${String.format("%.2f", amount)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            if (status.lowercase() != "bekliyor" && paymentDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ödeme Tarihi: ${dateFormat.format(paymentDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

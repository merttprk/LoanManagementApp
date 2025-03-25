package com.loanmanagementapp.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.theme.Blue80
import com.loanmanagementapp.theme.LightBlue80
import com.loanmanagementapp.theme.White
import java.text.NumberFormat
import java.util.Locale

/**
 * Kredi detaylarını gösteren kart bileşeni
 * @param loan Gösterilecek kredi
 * @param calculatedInterest Hesaplanan faiz tutarı
 * @param term Vade süresi (ay cinsinden)
 * @param containerColor Kart arka plan rengi
 * @param contentColor İçerik metin rengi
 * @param modifier Compose modifier
 */
@Composable
fun LoanDetailCard(
    loan: Loan,
    calculatedInterest: Double,
    term: Int = 12,
    containerColor: Color = White,
    contentColor: Color = Blue80,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
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
            Text(
                text = loan.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DetailRow(label = "Kredi Durumu", value = getStatusText(loan.status))
            DetailRow(label = "Ana Para", value = currencyFormat.format(loan.principalAmount))
            DetailRow(label = "Faiz Oranı", value = "%${loan.interestRate}")
            DetailRow(label = "Vade", value = "$term Ay")
            DetailRow(label = "Hesaplanan Faiz", value = currencyFormat.format(calculatedInterest))
            DetailRow(label = "Toplam Geri Ödeme", value = currencyFormat.format(loan.principalAmount + calculatedInterest))
            DetailRow(label = "Vade (Gün)", value = loan.dueIn.toString())
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = contentColor.copy(alpha = 0.2f)
            )
            
            Text(
                text = "Kredi ID: ${loan.id}",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Kredi detay satırı bileşeni
 * @param label Etiket
 * @param value Değer
 * @param modifier Compose modifier
 */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Kredi durumunu Türkçe metne çevirir
 * @param status Kredi durumu
 * @return Türkçe durum metni
 */
private fun getStatusText(status: String): String {
    return when (status.lowercase()) {
        "active" -> "Aktif"
        "paid" -> "Ödenmiş"
        "overdue" -> "Gecikmiş"
        "default" -> "Temerrüt"
        else -> status
    }
}

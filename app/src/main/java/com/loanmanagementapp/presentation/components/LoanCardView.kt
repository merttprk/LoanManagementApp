package com.loanmanagementapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.theme.Blue80
import com.loanmanagementapp.theme.LightBlue80
import java.text.NumberFormat
import java.util.Locale

/**
 * Kredi bilgilerini kart formatında gösteren bileşen
 * @param loan Gösterilecek kredi
 * @param onClick Karta tıklandığında çalışacak işlev (opsiyonel)
 * @param showDetails Detayları gösterip göstermeme durumu
 * @param modifier Compose modifier
 */
@Preview
@Composable
fun LoanCardPreview() {
    LoanCardView(loan = Loan(name = "Personal Loan", principalAmount = 10000.0, interestRate = 5.0, status = "active", dueIn = 30, id = "L1234", type = LoanType.PERSONAL))
}

@Composable
fun LoanCardView(
    loan: Loan,
    onClick: (() -> Unit)? = null,
    showDetails: Boolean = true,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    val statusColor = getStatusColor(loan.status)
    val statusText = getStatusText(loan.status)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
            // Kredi başlığı ve durum göstergesi
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = getLoanTypeText(loan.type),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Blue80,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = loan.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = statusColor,
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Ana para ve faiz oranı
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ana Para",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = currencyFormat.format(loan.principalAmount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Faiz Oranı",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "%${loan.interestRate}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LightBlue80
                    )
                }
            }
            
            if (showDetails) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.LightGray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))
                
                // Vade ve Kredi ID
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Vade (Gün)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = "${loan.dueIn}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Kredi ID",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = loan.id,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Kredi durumuna göre renk döndürür
 * @param status Kredi durumu
 * @return Durum rengi
 */
private fun getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "active" -> Color(0xFF4CAF50) // Yeşil
        "paid" -> Blue80             // Mavi
        "overdue" -> Color(0xFFF44336) // Kırmızı
        "default" -> Color(0xFF9C27B0) // Mor
        else -> Color.Gray
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

/**
 * Kredi türünü Türkçe metne çevirir
 * @param type Kredi türü
 * @return Türkçe kredi türü metni
 */
private fun getLoanTypeText(type: LoanType): String {
    return when (type) {
        LoanType.PERSONAL -> "Kişisel Kredi"
        LoanType.MORTGAGE -> "Konut Kredisi"
        LoanType.AUTO -> "Araba Kredisi"
        LoanType.BUSINESS -> "İşletme Kredisi"
        LoanType.EDUCATION -> "Eğitim Kredisi"
    }
}

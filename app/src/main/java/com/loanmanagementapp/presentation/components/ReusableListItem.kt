package com.loanmanagementapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.loanmanagementapp.theme.LightBlue80

/**
 * Yeniden kullanılabilir liste öğesi bileşeni
 * @param primaryText Ana metin
 * @param secondaryText İkincil metin
 * @param trailingText Sağ tarafta gösterilecek metin
 * @param backgroundColor Arka plan rengi
 * @param contentColor İçerik metin rengi
 * @param onClick Tıklama işlevi
 */
@Preview
@Composable
fun ReusableListItemPreview() {
    ReusableListItem(
        primaryText = "Primary Text",
        secondaryText = "Secondary Text",
        trailingText = "Trailing Text",
        onClick = {},
        backgroundColor = LightBlue80
    )
}

@Composable
fun ReusableListItem(
    primaryText: String,
    secondaryText: String,
    trailingText: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = primaryText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = secondaryText,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        trailingText?.let {
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}
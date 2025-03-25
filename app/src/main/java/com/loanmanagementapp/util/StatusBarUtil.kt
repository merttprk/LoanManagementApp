package com.loanmanagementapp.util

import android.app.Activity
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Status bar rengini ve görünürlüğünü ayarlamak için yardımcı fonksiyonlar
 */
object StatusBarUtil {
    
    /**
     * Status bar rengini ayarlar
     * @param color Status bar rengi
     * @param darkIcons Status bar ikonlarının koyu renkte olup olmayacağı
     */
    @Composable
    fun SetStatusBarColor(color: Color, darkIcons: Boolean = true) {
        val view = LocalView.current
        if (!view.isInEditMode) {
            DisposableEffect(color, darkIcons) {
                val window = (view.context as Activity).window
                window.statusBarColor = color.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkIcons
                
                onDispose {
                    // Varsayılan ayarlara geri dön
                }
            }
        }
    }
    
    /**
     * Status bar'ı şeffaf yapar
     * @param darkIcons Status bar ikonlarının koyu renkte olup olmayacağı
     */
    @Composable
    fun SetTransparentStatusBar(darkIcons: Boolean = true) {
        val view = LocalView.current
        if (!view.isInEditMode) {
            DisposableEffect(darkIcons) {
                val window = (view.context as Activity).window
                window.statusBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkIcons
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
                
                onDispose {
                    // Varsayılan ayarlara geri dön
                    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                }
            }
        }
    }
}

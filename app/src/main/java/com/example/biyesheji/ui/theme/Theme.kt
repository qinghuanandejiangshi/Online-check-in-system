package com.example.biyesheji.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    // 主色
    primary = SkyBlue,        // 天空蓝作为主色
    secondary = FreshLavender, // 清新薰衣草色作为次要色
    tertiary = MintGreen,      // 薄荷绿作为第三色
    
    background = Color(0xFFF5F7FA),  // 更亮、清爽的背景色
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    
    // 变体和强调色
    surfaceVariant = LavenderBlue,
    outlineVariant = SoftPurple,
    inversePrimary = PastelYellow,
    
    // 特殊用途颜色
    error = Red500,
    surfaceTint = BabyBlue
)

private val DarkColorScheme = darkColorScheme(
    // 主色
    primary = SkyBlue.copy(alpha = 0.85f),  // 稍微降低亮度的天空蓝
    secondary = FreshLavender.copy(alpha = 0.85f), // 稍微降低亮度的薰衣草色
    tertiary = MintGreen.copy(alpha = 0.85f),  // 稍微降低亮度的薄荷绿
    
    background = Color(0xFF121212),  // 深色背景
    surface = Color(0xFF1E1E1E),    // 深灰色表面
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFFFFBFF),
    onSurface = Color(0xFFFFFBFF),
    
    // 变体和强调色
    surfaceVariant = Color(0xFF31333A),
    outlineVariant = SoftPurple.copy(alpha = 0.6f),
    inversePrimary = PastelYellow.copy(alpha = 0.7f),
    
    // 特殊用途颜色
    error = Red500,
    surfaceTint = BabyBlue.copy(alpha = 0.7f)
)

@Composable
fun BiyeshejiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
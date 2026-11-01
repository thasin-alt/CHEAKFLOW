package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.VioletGlow

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    glowBorder: Boolean = false,
    elevation: Dp = 6.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f

    val surfaceColor = if (isDark) {
        Color(0xCC111827)
    } else {
        Color(0xEBFFFFFF)
    }

    val borderBrush = if (glowBorder) {
        Brush.linearGradient(
            colors = listOf(
                CyanGlow.copy(alpha = if (isDark) 0.65f else 0.45f),
                VioletGlow.copy(alpha = if (isDark) 0.45f else 0.35f),
                Color.Transparent
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                if (isDark) Color(0x38FFFFFF) else Color(0x30FFFFFF),
                if (isDark) Color(0x12FFFFFF) else Color(0x18000000)
            )
        )
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (glowBorder) CyanGlow.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.15f),
                spotColor = if (glowBorder) VioletGlow.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.2f)
            )
            .clip(shape)
            .background(surfaceColor)
            .border(
                border = BorderStroke(1.2.dp, borderBrush),
                shape = shape
            ),
        content = content
    )
}

@Composable
fun glassGlowBrush(isDark: Boolean): Brush {
    return Brush.linearGradient(
        colors = listOf(
            CyanGlow.copy(alpha = if (isDark) 0.8f else 0.9f),
            VioletGlow.copy(alpha = if (isDark) 0.85f else 0.95f)
        )
    )
}

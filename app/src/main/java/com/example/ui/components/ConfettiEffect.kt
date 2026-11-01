package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.VioletGlow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    val angle: Double,
    val speed: Float,
    val radius: Float,
    val color: Color
)

@Composable
fun CelebrationConfetti(
    triggerTime: Long,
    modifier: Modifier = Modifier
) {
    if (triggerTime == 0L) return

    val progress = remember(triggerTime) { Animatable(0f) }

    LaunchedEffect(triggerTime) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        )
    }

    if (progress.value >= 1f) return

    val particles = remember(triggerTime) {
        val colors = listOf(CyanGlow, VioletGlow, EmeraldSuccess, Color(0xFFFBBF24), Color(0xFFF472B6))
        List(28) {
            Particle(
                angle = Random.nextDouble(0.0, 2 * Math.PI),
                speed = Random.nextFloat() * 180f + 60f,
                radius = Random.nextFloat() * 5f + 3f,
                color = colors[Random.nextInt(colors.size)]
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val currentProgress = progress.value
        val alpha = (1f - currentProgress).coerceIn(0f, 1f)

        particles.forEach { particle ->
            val dist = particle.speed * currentProgress
            val x = center.x + (cos(particle.angle) * dist).toFloat()
            val y = center.y + (sin(particle.angle) * dist).toFloat() + (currentProgress * currentProgress * 40f)

            drawCircle(
                color = particle.color.copy(alpha = alpha),
                radius = particle.radius * (1f - currentProgress * 0.4f),
                center = Offset(x, y)
            )
        }
    }
}

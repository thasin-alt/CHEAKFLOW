package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Splitscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FilterMode
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.VioletGlow

@Composable
fun ChecklistProgressCard(
    totalTasks: Int,
    completedTasks: Int,
    filterMode: FilterMode,
    onFilterSelected: (FilterMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingTasks = (totalTasks - completedTasks).coerceAtLeast(0)
    val percentage = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "progress_anim"
    )

    val headlineText = when {
        totalTasks == 0 -> "No tasks yet"
        completedTasks == totalTasks -> "All tasks completed! ✨"
        percentage >= 0.5f -> "More than halfway there! 🚀"
        else -> "Keep up the momentum! ⚡"
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("progress_card"),
        glowBorder = true,
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Overview",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = headlineText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$completedTasks of $totalTasks items finished",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Circular Progress Ring
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(76.dp)
                ) {
                    val trackColor = MaterialTheme.colorScheme.outlineVariant
                    val progressGradient = Brush.sweepGradient(
                        colors = listOf(CyanGlow, VioletGlow, EmeraldSuccess, CyanGlow)
                    )

                    Canvas(modifier = Modifier.size(72.dp)) {
                        val strokeWidth = 7.dp.toPx()
                        // Track
                        drawCircle(
                            color = trackColor,
                            radius = (size.minDimension - strokeWidth) / 2,
                            style = Stroke(width = strokeWidth)
                        )
                        // Progress Arc
                        drawArc(
                            brush = progressGradient,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Filter Tabs (All / Need to Do / Done)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterTabButton(
                    title = "All",
                    count = totalTasks,
                    selected = filterMode == FilterMode.ALL,
                    onClick = { onFilterSelected(FilterMode.ALL) },
                    modifier = Modifier.weight(1f),
                    testTag = "filter_all"
                )
                FilterTabButton(
                    title = "To Do",
                    count = pendingTasks,
                    selected = filterMode == FilterMode.NEED_TO_DO,
                    onClick = { onFilterSelected(FilterMode.NEED_TO_DO) },
                    modifier = Modifier.weight(1f),
                    testTag = "filter_need_to_do"
                )
                FilterTabButton(
                    title = "Done",
                    count = completedTasks,
                    selected = filterMode == FilterMode.DONE,
                    onClick = { onFilterSelected(FilterMode.DONE) },
                    modifier = Modifier.weight(1f),
                    testTag = "filter_done"
                )
            }
        }
    }
}

@Composable
private fun FilterTabButton(
    title: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val bg = if (selected) {
        Brush.horizontalGradient(listOf(CyanGlow.copy(alpha = 0.25f), VioletGlow.copy(alpha = 0.25f)))
    } else {
        Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.18f))
                    .padding(horizontal = 6.dp, vertical = 1.dp)
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}

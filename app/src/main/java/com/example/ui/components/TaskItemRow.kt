package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TaskEntity
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.VioletGlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskItemRow(
    task: TaskEntity,
    onToggleDone: (TaskEntity) -> Unit,
    onEditTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val checkScale by animateFloatAsState(
        targetValue = if (task.isDone) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "check_scale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (task.isDone) 0.55f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "content_alpha"
    )

    val priorityColor = when (task.priority.uppercase()) {
        "HIGH" -> PriorityHigh
        "LOW" -> PriorityLow
        else -> PriorityMedium
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}"),
        glowBorder = !task.isDone && task.priority == "HIGH",
        elevation = if (task.isDone) 2.dp else 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEditTask(task) }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interactive Glass Checkbox with Bouncy Animation
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onToggleDone(task) }
                    .testTag("task_checkbox_${task.id}"),
                contentAlignment = Alignment.Center
            ) {
                val checkGradient = Brush.linearGradient(listOf(CyanGlow, VioletGlow))
                val isDark = MaterialTheme.colorScheme.background.red < 0.2f

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .scale(checkScale)
                        .clip(CircleShape)
                        .then(
                            if (task.isDone) {
                                Modifier.background(checkGradient)
                            } else {
                                Modifier
                                    .background(if (isDark) Color(0x22FFFFFF) else Color(0x18000000))
                                    .border(
                                        width = 1.8.dp,
                                        brush = Brush.linearGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isDone) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Completed",
                            tint = Color(0xFF041E26),
                            modifier = Modifier
                                .size(18.dp)
                                .scale(checkScale)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Task Details Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (task.isDone) FontWeight.Normal else FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // Priority indicator dot/chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(priorityColor.copy(alpha = if (task.isDone) 0.15f else 0.25f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.priority.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = priorityColor.copy(alpha = if (task.isDone) 0.6f else 1f)
                        )
                    }
                }

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha * 0.85f),
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (task.dueDateMillis != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val formattedDate = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                        .format(Date(task.dueDateMillis))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = "Due date",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = contentAlpha),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Due $formattedDate",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = contentAlpha)
                        )
                    }
                }
            }

            // Quick Actions
            IconButton(
                onClick = { onEditTask(task) },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("task_edit_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit task",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = { onDeleteTask(task) },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("task_delete_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

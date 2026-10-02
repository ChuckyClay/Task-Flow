package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Priority
import com.example.data.Task
import com.example.data.TaskCategory

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskItemCard(
    task: Task,
    onToggleCompletion: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val cardAlpha by animateFloatAsState(
        targetValue = if (task.isCompleted) 0.65f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "cardAlpha"
    )

    val checkScale by animateFloatAsState(
        targetValue = if (task.isCompleted) 1.1f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "checkScale"
    )

    val checkboxBgColor by animateColorAsState(
        targetValue = if (task.isCompleted) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 200),
        label = "checkboxBgColor"
    )

    val checkboxBorderColor by animateColorAsState(
        targetValue = if (task.isCompleted) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(durationMillis = 200),
        label = "checkboxBorderColor"
    )

    val isOverdue = task.isOverdue

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(cardAlpha)
            .testTag("task_item_${task.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (task.isCompleted) 0.dp else 1.5.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isOverdue && !task.isCompleted) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEdit() }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Custom Checkbox with spring animation and smooth fill
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp, end = 12.dp)
                        .size(26.dp)
                        .scale(checkScale)
                        .clip(CircleShape)
                        .background(checkboxBgColor)
                        .clickable { onToggleCompletion() }
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(
                            if (task.isCompleted) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                        .testTag("task_checkbox_${task.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (!task.isCompleted) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(checkboxBorderColor.copy(alpha = 0.25f))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Task Content (Title & Description)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (task.isCompleted) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = if (task.isCompleted) 0.5f else 0.85f
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.padding(start = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("task_edit_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("task_delete_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.85f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata Tags: Priority Badge, Category Chip, Due Date Chip
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Priority Badge
                PriorityBadge(priority = task.priority, isDark = isDark)

                // Category Badge
                CategoryBadge(category = task.category, isDark = isDark)

                // Due Date Badge
                task.formattedDueDate?.let { dueFormatted ->
                    DueDateBadge(
                        formattedDate = dueFormatted,
                        isOverdue = isOverdue && !task.isCompleted,
                        isDueToday = task.isDueToday && !task.isCompleted
                    )
                }
            }
        }
    }
}

@Composable
private fun PriorityBadge(priority: Priority, isDark: Boolean) {
    val pillBg = if (isDark) priority.containerDark else priority.containerLight
    val pillText = if (isDark) priority.darkColor else priority.lightColor

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(pillBg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(pillText)
            )
            Text(
                text = "${priority.title} Priority",
                style = MaterialTheme.typography.labelSmall,
                color = pillText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun CategoryBadge(category: TaskCategory, isDark: Boolean) {
    val pillBg = if (isDark) category.containerDark else category.containerLight
    val pillColor = if (isDark) category.colorDark else category.colorLight

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(pillBg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = pillColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = category.title,
                style = MaterialTheme.typography.labelSmall,
                color = pillColor,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun DueDateBadge(
    formattedDate: String,
    isOverdue: Boolean,
    isDueToday: Boolean
) {
    val (bgColor, textColor, icon) = when {
        isOverdue -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            Icons.Default.Warning
        )
        isDueToday -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            Icons.Default.AccessTime
        )
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            Icons.Default.AccessTime
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = if (isOverdue) "Overdue ($formattedDate)" else formattedDate,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = if (isOverdue || isDueToday) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 11.sp
            )
        }
    }
}

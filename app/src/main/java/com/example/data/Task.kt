package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null,
    val dueTimeHour: Int? = null,
    val dueTimeMinute: Int? = null,
    val priority: Priority = Priority.MEDIUM,
    val category: TaskCategory = TaskCategory.PERSONAL,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    val formattedDueDate: String?
        get() {
            if (dueDate == null) return null
            val nowCal = Calendar.getInstance()
            val dueCal = Calendar.getInstance().apply { timeInMillis = dueDate }

            val isToday = nowCal.get(Calendar.YEAR) == dueCal.get(Calendar.YEAR) &&
                    nowCal.get(Calendar.DAY_OF_YEAR) == dueCal.get(Calendar.DAY_OF_YEAR)

            val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
            val isTomorrow = tomorrowCal.get(Calendar.YEAR) == dueCal.get(Calendar.YEAR) &&
                    tomorrowCal.get(Calendar.DAY_OF_YEAR) == dueCal.get(Calendar.DAY_OF_YEAR)

            val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val isYesterday = yesterdayCal.get(Calendar.YEAR) == dueCal.get(Calendar.YEAR) &&
                    yesterdayCal.get(Calendar.DAY_OF_YEAR) == dueCal.get(Calendar.DAY_OF_YEAR)

            val dateStr = when {
                isToday -> "Today"
                isTomorrow -> "Tomorrow"
                isYesterday -> "Yesterday"
                else -> {
                    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    sdf.format(Date(dueDate))
                }
            }

            return if (dueTimeHour != null && dueTimeMinute != null) {
                val timeStr = String.format(
                    Locale.getDefault(),
                    "%02d:%02d %s",
                    if (dueTimeHour % 12 == 0) 12 else dueTimeHour % 12,
                    dueTimeMinute,
                    if (dueTimeHour < 12) "AM" else "PM"
                )
                "$dateStr at $timeStr"
            } else {
                dateStr
            }
        }

    val isOverdue: Boolean
        get() {
            if (isCompleted || dueDate == null) return false
            val endOfDay = Calendar.getInstance().apply {
                timeInMillis = dueDate
                if (dueTimeHour != null && dueTimeMinute != null) {
                    set(Calendar.HOUR_OF_DAY, dueTimeHour)
                    set(Calendar.MINUTE, dueTimeMinute)
                    set(Calendar.SECOND, 0)
                } else {
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }
            }.timeInMillis
            return System.currentTimeMillis() > endOfDay
        }

    val isDueToday: Boolean
        get() {
            if (dueDate == null) return false
            val nowCal = Calendar.getInstance()
            val dueCal = Calendar.getInstance().apply { timeInMillis = dueDate }
            return nowCal.get(Calendar.YEAR) == dueCal.get(Calendar.YEAR) &&
                    nowCal.get(Calendar.DAY_OF_YEAR) == dueCal.get(Calendar.DAY_OF_YEAR)
        }
}

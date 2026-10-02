package com.example.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class TaskCategory(
    val title: String,
    val icon: ImageVector,
    val colorLight: Color,
    val colorDark: Color,
    val containerLight: Color,
    val containerDark: Color
) {
    WORK(
        title = "Work",
        icon = Icons.Default.Work,
        colorLight = Color(0xFF4338CA),
        colorDark = Color(0xFF818CF8),
        containerLight = Color(0xFFE0E7FF),
        containerDark = Color(0xFF312E81)
    ),
    PERSONAL(
        title = "Personal",
        icon = Icons.Default.Person,
        colorLight = Color(0xFF7E22CE),
        colorDark = Color(0xFFC084FC),
        containerLight = Color(0xFFF3E8FF),
        containerDark = Color(0xFF581C87)
    ),
    SHOPPING(
        title = "Shopping",
        icon = Icons.Default.ShoppingCart,
        colorLight = Color(0xFFB45309),
        colorDark = Color(0xFFFCD34D),
        containerLight = Color(0xFFFEF3C7),
        containerDark = Color(0xFF78350F)
    ),
    HEALTH(
        title = "Health",
        icon = Icons.Default.FitnessCenter,
        colorLight = Color(0xFFBE123C),
        colorDark = Color(0xFFFB7185),
        containerLight = Color(0xFFFFE4E6),
        containerDark = Color(0xFF881337)
    ),
    LEARNING(
        title = "Learning",
        icon = Icons.Default.School,
        colorLight = Color(0xFF0369A1),
        colorDark = Color(0xFF38BDF8),
        containerLight = Color(0xFFE0F2FE),
        containerDark = Color(0xFF0C4A6E)
    ),
    FINANCE(
        title = "Finance",
        icon = Icons.Default.AccountBalance,
        colorLight = Color(0xFF047857),
        colorDark = Color(0xFF34D399),
        containerLight = Color(0xFFD1FAE5),
        containerDark = Color(0xFF064E3B)
    ),
    OTHER(
        title = "Other",
        icon = Icons.Default.Bookmark,
        colorLight = Color(0xFF475569),
        colorDark = Color(0xFF94A3B8),
        containerLight = Color(0xFFF1F5F9),
        containerDark = Color(0xFF334155)
    );

    companion object {
        fun fromString(value: String): TaskCategory {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}

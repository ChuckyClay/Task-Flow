package com.example.data

import androidx.compose.ui.graphics.Color

enum class Priority(
    val title: String,
    val lightColor: Color,
    val darkColor: Color,
    val containerLight: Color,
    val containerDark: Color
) {
    LOW(
        title = "Low",
        lightColor = Color(0xFF0D9488), // Teal-600
        darkColor = Color(0xFF2DD4BF),  // Teal-400
        containerLight = Color(0xFFCCFBF1),
        containerDark = Color(0xFF134E4A)
    ),
    MEDIUM(
        title = "Medium",
        lightColor = Color(0xFFD97706), // Amber-600
        darkColor = Color(0xFFFBBF24),  // Amber-400
        containerLight = Color(0xFFFEF3C7),
        containerDark = Color(0xFF78350F)
    ),
    HIGH(
        title = "High",
        lightColor = Color(0xFFDC2626), // Red-600
        darkColor = Color(0xFFF87171),  // Red-400
        containerLight = Color(0xFFFEE2E2),
        containerDark = Color(0xFF7F1D1D)
    );

    companion object {
        fun fromString(value: String): Priority {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}

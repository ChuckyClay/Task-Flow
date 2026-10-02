package com.example.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.fromString(value)

    @TypeConverter
    fun fromCategory(category: TaskCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): TaskCategory = TaskCategory.fromString(value)
}

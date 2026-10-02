package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(entities = [Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "taskflow_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial sample tasks on first launch
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(INSTANCE?.taskDao())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(dao: TaskDao?) {
            if (dao == null) return
            val today = Calendar.getInstance()
            val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

            val initialTasks = listOf(
                Task(
                    title = "Review project deliverables",
                    description = "Verify sprint goals and finalize presentation slides for team meeting.",
                    dueDate = today.timeInMillis,
                    dueTimeHour = 15,
                    dueTimeMinute = 30,
                    priority = Priority.HIGH,
                    category = TaskCategory.WORK,
                    isCompleted = false
                ),
                Task(
                    title = "Buy fresh vegetables and fruits",
                    description = "Spinach, avocados, organic bananas, almond milk.",
                    dueDate = today.timeInMillis,
                    dueTimeHour = 18,
                    dueTimeMinute = 0,
                    priority = Priority.MEDIUM,
                    category = TaskCategory.SHOPPING,
                    isCompleted = true,
                    completedAt = System.currentTimeMillis() - 3600000
                ),
                Task(
                    title = "Explore Material 3 Compose guidelines",
                    description = "Study latest dynamic color system, adaptive layouts, and gesture animations.",
                    dueDate = tomorrow.timeInMillis,
                    dueTimeHour = 10,
                    dueTimeMinute = 0,
                    priority = Priority.MEDIUM,
                    category = TaskCategory.LEARNING,
                    isCompleted = false
                ),
                Task(
                    title = "30-Minute evening cardio workout",
                    description = "Warm up, interval running on treadmill, and post-stretch.",
                    dueDate = today.timeInMillis,
                    dueTimeHour = 19,
                    dueTimeMinute = 30,
                    priority = Priority.LOW,
                    category = TaskCategory.HEALTH,
                    isCompleted = false
                ),
                Task(
                    title = "Monthly budget check-in",
                    description = "Review savings progress, bills, and monthly subscriptions.",
                    dueDate = tomorrow.timeInMillis,
                    dueTimeHour = 14,
                    dueTimeMinute = 0,
                    priority = Priority.HIGH,
                    category = TaskCategory.FINANCE,
                    isCompleted = false
                )
            )
            dao.insertTasks(initialTasks)
        }
    }
}

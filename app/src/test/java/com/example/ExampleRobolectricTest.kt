package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.Priority
import com.example.data.Task
import com.example.data.TaskCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TaskFlow", appName)
    }

    @Test
    fun `task isDueToday returns true for current date`() {
        val today = Calendar.getInstance().timeInMillis
        val task = Task(
            title = "Test Task",
            dueDate = today,
            priority = Priority.HIGH,
            category = TaskCategory.WORK
        )
        assertTrue(task.isDueToday)
    }

    @Test
    fun `task isOverdue returns true for yesterday timestamp`() {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -2)
        }.timeInMillis
        val task = Task(
            title = "Overdue Task",
            dueDate = yesterday,
            isCompleted = false
        )
        assertTrue(task.isOverdue)
    }

    @Test
    fun `completed task is never overdue`() {
        val pastDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -5)
        }.timeInMillis
        val task = Task(
            title = "Finished Task",
            dueDate = pastDate,
            isCompleted = true
        )
        assertFalse(task.isOverdue)
    }
}

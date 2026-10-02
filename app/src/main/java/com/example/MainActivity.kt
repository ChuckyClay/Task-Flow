package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.TaskDatabase
import com.example.data.TaskRepository
import com.example.ui.TaskScreen
import com.example.ui.TaskViewModel
import com.example.ui.theme.TaskFlowTheme

class MainActivity : ComponentActivity() {

    private val database by lazy { TaskDatabase.getDatabase(this) }
    private val repository by lazy { TaskRepository(database.taskDao()) }

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModel.provideFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskFlowTheme {
                TaskScreen(viewModel = viewModel)
            }
        }
    }
}

package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Priority
import com.example.data.Task
import com.example.data.TaskCategory
import com.example.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class StatusFilter(val label: String) {
    ALL("All"),
    ACTIVE("Active"),
    COMPLETED("Done")
}

enum class SortBy(val label: String) {
    DUE_DATE("Due Date"),
    PRIORITY("Priority"),
    CREATED_AT("Recently Added"),
    TITLE("Alphabetical")
}

data class TaskFilter(
    val query: String = "",
    val status: StatusFilter = StatusFilter.ALL,
    val category: TaskCategory? = null,
    val priority: Priority? = null,
    val sortBy: SortBy = SortBy.DUE_DATE
)

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val totalCount: Int = 0,
    val activeCount: Int = 0,
    val completedCount: Int = 0,
    val dueTodayRemainingCount: Int = 0,
    val progressPercentage: Float = 0f,
    val searchQuery: String = "",
    val statusFilter: StatusFilter = StatusFilter.ALL,
    val selectedCategory: TaskCategory? = null,
    val selectedPriority: Priority? = null,
    val sortBy: SortBy = SortBy.DUE_DATE,
    val isAddEditSheetOpen: Boolean = false,
    val editingTask: Task? = null,
    val taskToDelete: Task? = null,
    val categoryCounts: Map<TaskCategory, Int> = emptyMap()
)

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter())
    private val _isAddEditSheetOpen = MutableStateFlow(false)
    private val _editingTask = MutableStateFlow<Task?>(null)
    private val _taskToDelete = MutableStateFlow<Task?>(null)
    private var lastDeletedTask: Task? = null

    val uiState: StateFlow<TaskUiState> = combine(
        repository.allTasks,
        _filter,
        _isAddEditSheetOpen,
        _editingTask,
        _taskToDelete
    ) { allTasks, filter, isAddEditOpen, editing, toDelete ->
        val totalCount = allTasks.size
        val activeCount = allTasks.count { !it.isCompleted }
        val completedCount = allTasks.count { it.isCompleted }
        val dueTodayRemainingCount = allTasks.count { it.isDueToday && !it.isCompleted }
        val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

        val categoryCounts = TaskCategory.entries.associateWith { cat ->
            allTasks.count { it.category == cat }
        }

        // Apply filters
        val filteredList = allTasks.filter { task ->
            val matchesStatus = when (filter.status) {
                StatusFilter.ALL -> true
                StatusFilter.ACTIVE -> !task.isCompleted
                StatusFilter.COMPLETED -> task.isCompleted
            }
            val matchesCategory = filter.category == null || task.category == filter.category
            val matchesPriority = filter.priority == null || task.priority == filter.priority
            val matchesQuery = filter.query.isBlank() ||
                    task.title.contains(filter.query, ignoreCase = true) ||
                    task.description.contains(filter.query, ignoreCase = true) ||
                    task.category.title.contains(filter.query, ignoreCase = true)

            matchesStatus && matchesCategory && matchesPriority && matchesQuery
        }

        // Apply sorting
        val sortedList = when (filter.sortBy) {
            SortBy.DUE_DATE -> filteredList.sortedWith(
                compareBy<Task> { it.isCompleted }
                    .thenBy { it.dueDate ?: Long.MAX_VALUE }
                    .thenByDescending { it.priority.ordinal }
            )
            SortBy.PRIORITY -> filteredList.sortedWith(
                compareBy<Task> { it.isCompleted }
                    .thenByDescending { it.priority.ordinal }
                    .thenBy { it.dueDate ?: Long.MAX_VALUE }
            )
            SortBy.CREATED_AT -> filteredList.sortedWith(
                compareBy<Task> { it.isCompleted }
                    .thenByDescending { it.createdAt }
            )
            SortBy.TITLE -> filteredList.sortedWith(
                compareBy<Task> { it.isCompleted }
                    .thenBy { it.title.lowercase() }
            )
        }

        TaskUiState(
            tasks = sortedList,
            totalCount = totalCount,
            activeCount = activeCount,
            completedCount = completedCount,
            dueTodayRemainingCount = dueTodayRemainingCount,
            progressPercentage = progress,
            searchQuery = filter.query,
            statusFilter = filter.status,
            selectedCategory = filter.category,
            selectedPriority = filter.priority,
            sortBy = filter.sortBy,
            isAddEditSheetOpen = isAddEditOpen,
            editingTask = editing,
            taskToDelete = toDelete,
            categoryCounts = categoryCounts
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState()
    )

    fun onSearchQueryChange(query: String) {
        _filter.update { it.copy(query = query) }
    }

    fun onStatusFilterChange(status: StatusFilter) {
        _filter.update { it.copy(status = status) }
    }

    fun onCategoryFilterChange(category: TaskCategory?) {
        _filter.update { current ->
            current.copy(category = if (current.category == category) null else category)
        }
    }

    fun onPriorityFilterChange(priority: Priority?) {
        _filter.update { current ->
            current.copy(priority = if (current.priority == priority) null else priority)
        }
    }

    fun onSortByChange(sortBy: SortBy) {
        _filter.update { it.copy(sortBy = sortBy) }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun openAddTaskSheet() {
        _editingTask.value = null
        _isAddEditSheetOpen.value = true
    }

    fun openEditTaskSheet(task: Task) {
        _editingTask.value = task
        _isAddEditSheetOpen.value = true
    }

    fun closeAddEditSheet() {
        _isAddEditSheetOpen.value = false
        _editingTask.value = null
    }

    fun saveTask(
        title: String,
        description: String,
        dueDate: Long?,
        dueTimeHour: Int?,
        dueTimeMinute: Int?,
        priority: Priority,
        category: TaskCategory
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            val current = _editingTask.value
            if (current != null) {
                repository.updateTask(
                    current.copy(
                        title = title.trim(),
                        description = description.trim(),
                        dueDate = dueDate,
                        dueTimeHour = dueTimeHour,
                        dueTimeMinute = dueTimeMinute,
                        priority = priority,
                        category = category
                    )
                )
            } else {
                repository.insertTask(
                    Task(
                        title = title.trim(),
                        description = description.trim(),
                        dueDate = dueDate,
                        dueTimeHour = dueTimeHour,
                        dueTimeMinute = dueTimeMinute,
                        priority = priority,
                        category = category
                    )
                )
            }
            closeAddEditSheet()
        }
    }

    fun requestDeleteTask(task: Task) {
        _taskToDelete.value = task
    }

    fun dismissDeleteTask() {
        _taskToDelete.value = null
    }

    fun confirmDeleteTask(onDeleted: ((Task) -> Unit)? = null) {
        val task = _taskToDelete.value ?: return
        lastDeletedTask = task
        viewModelScope.launch {
            repository.deleteTask(task)
            _taskToDelete.value = null
            onDeleted?.invoke(task)
        }
    }

    fun undoDelete() {
        val taskToRestore = lastDeletedTask ?: return
        viewModelScope.launch {
            repository.insertTask(taskToRestore)
            lastDeletedTask = null
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            repository.clearCompletedTasks()
        }
    }

    companion object {
        fun provideFactory(repository: TaskRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TaskViewModel(repository) as T
                }
            }
    }
}

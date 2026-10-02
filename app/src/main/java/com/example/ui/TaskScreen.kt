package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Priority
import com.example.ui.components.AddEditTaskSheet
import com.example.ui.components.CategoryFilterChips
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.ProgressSummaryCard
import com.example.ui.components.StatusFilterTabs
import com.example.ui.components.TaskItemCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showSortMenu by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showPriorityMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "TaskFlow",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    // Priority Filter Menu Button
                    Box {
                        IconButton(
                            onClick = { showPriorityMenu = true },
                            modifier = Modifier.testTag("priority_filter_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter by Priority",
                                tint = if (uiState.selectedPriority != null) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                        DropdownMenu(
                            expanded = showPriorityMenu,
                            onDismissRequest = { showPriorityMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Priorities") },
                                onClick = {
                                    viewModel.onPriorityFilterChange(null)
                                    showPriorityMenu = false
                                }
                            )
                            Priority.entries.forEach { priority ->
                                DropdownMenuItem(
                                    text = { Text("${priority.title} Priority") },
                                    onClick = {
                                        viewModel.onPriorityFilterChange(priority)
                                        showPriorityMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Sort Menu Button
                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier.testTag("sort_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort Tasks",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortBy.entries.forEach { sort ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = sort.label,
                                            fontWeight = if (uiState.sortBy == sort) FontWeight.Bold else FontWeight.Normal,
                                            color = if (uiState.sortBy == sort) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        viewModel.onSortByChange(sort)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // More Options Menu Button
                    Box {
                        IconButton(
                            onClick = { showOptionsMenu = true },
                            modifier = Modifier.testTag("more_options_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Clear Completed Tasks") },
                                onClick = {
                                    viewModel.clearCompletedTasks()
                                    showOptionsMenu = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddTaskSheet() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = {
                    Text(
                        text = "New Task",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("add_task_fab")
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = {
                        Text(
                            text = "Search tasks, notes, categories…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.onSearchQueryChange("") },
                                modifier = Modifier.testTag("clear_search_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_tasks_input")
                )
            }

            // Tasks List with Header, Summary, Tabs, and Items
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("tasks_lazy_column"),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Progress Summary Card (shown when not searching)
                if (uiState.searchQuery.isBlank()) {
                    item(key = "progress_card") {
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            ProgressSummaryCard(
                                totalCount = uiState.totalCount,
                                activeCount = uiState.activeCount,
                                completedCount = uiState.completedCount,
                                dueTodayRemainingCount = uiState.dueTodayRemainingCount,
                                progressPercentage = uiState.progressPercentage
                            )
                        }
                    }
                }

                // Status Filter Tabs (All / Active / Done)
                item(key = "status_tabs") {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        StatusFilterTabs(
                            selectedFilter = uiState.statusFilter,
                            allCount = uiState.totalCount,
                            activeCount = uiState.activeCount,
                            completedCount = uiState.completedCount,
                            onFilterSelected = { viewModel.onStatusFilterChange(it) }
                        )
                    }
                }

                // Category Filter Chips Row
                item(key = "category_chips") {
                    CategoryFilterChips(
                        selectedCategory = uiState.selectedCategory,
                        categoryCounts = uiState.categoryCounts,
                        onCategorySelected = { viewModel.onCategoryFilterChange(it) }
                    )
                }

                // Active Priority Filter indicator if any
                if (uiState.selectedPriority != null) {
                    item(key = "priority_indicator") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Filtered by: ${uiState.selectedPriority?.title} Priority",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            IconButton(
                                onClick = { viewModel.onPriorityFilterChange(null) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear priority filter",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Empty State or Task Items
                if (uiState.tasks.isEmpty()) {
                    item(key = "empty_state") {
                        EmptyStateView(
                            searchQuery = uiState.searchQuery,
                            statusFilter = uiState.statusFilter,
                            onResetFilters = {
                                viewModel.onSearchQueryChange("")
                                viewModel.onStatusFilterChange(StatusFilter.ALL)
                                viewModel.onCategoryFilterChange(null)
                                viewModel.onPriorityFilterChange(null)
                            },
                            onAddTask = { viewModel.openAddTaskSheet() }
                        )
                    }
                } else {
                    items(
                        items = uiState.tasks,
                        key = { it.id }
                    ) { task ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        ) {
                            TaskItemCard(
                                task = task,
                                onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                                onEdit = { viewModel.openEditTaskSheet(task) },
                                onDelete = { viewModel.requestDeleteTask(task) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Add/Edit Task
    if (uiState.isAddEditSheetOpen) {
        AddEditTaskSheet(
            task = uiState.editingTask,
            onDismiss = { viewModel.closeAddEditSheet() },
            onSave = { title, desc, dueDate, dueHour, dueMinute, priority, category ->
                viewModel.saveTask(
                    title = title,
                    description = desc,
                    dueDate = dueDate,
                    dueTimeHour = dueHour,
                    dueTimeMinute = dueMinute,
                    priority = priority,
                    category = category
                )
            }
        )
    }

    // Delete Confirmation Dialog
    uiState.taskToDelete?.let { task ->
        DeleteConfirmationDialog(
            task = task,
            onConfirm = {
                viewModel.confirmDeleteTask { deletedTask ->
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "\"${deletedTask.title}\" deleted",
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.undoDelete()
                        }
                    }
                }
            },
            onDismiss = { viewModel.dismissDeleteTask() }
        )
    }
}

@Composable
private fun EmptyStateView(
    searchQuery: String,
    statusFilter: StatusFilter,
    onResetFilters: () -> Unit,
    onAddTask: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 40.dp, start = 32.dp, end = 32.dp)
            .testTag("empty_state_view"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when {
                    searchQuery.isNotBlank() -> Icons.Default.Search
                    statusFilter == StatusFilter.COMPLETED -> Icons.Default.CheckCircleOutline
                    else -> Icons.Default.TaskAlt
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                searchQuery.isNotBlank() -> "No matching tasks found"
                statusFilter == StatusFilter.COMPLETED -> "No completed tasks yet"
                statusFilter == StatusFilter.ACTIVE -> "No active tasks!"
                else -> "Your list is empty"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when {
                searchQuery.isNotBlank() -> "Try searching with different keywords or clear your search query."
                statusFilter == StatusFilter.COMPLETED -> "Mark tasks as done to see them here."
                statusFilter == StatusFilter.ACTIVE -> "All tasks are completed! Great job."
                else -> "Stay organized and productive. Tap the button below to add your first task."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (searchQuery.isNotBlank()) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onResetFilters() }
                    .testTag("clear_filter_button"),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Clear Filters",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

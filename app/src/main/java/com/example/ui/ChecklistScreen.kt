package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ClearAll
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ChecklistEntity
import com.example.data.local.TaskEntity
import com.example.ui.components.AddEditTaskBottomSheet
import com.example.ui.components.CelebrationConfetti
import com.example.ui.components.ChecklistProgressCard
import com.example.ui.components.CreateListDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.TaskItemRow
import com.example.ui.components.getChecklistIcon
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.VioletGlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    viewModel: ChecklistViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddTaskSheet by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }
    var showCreateListDialog by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Handle Undo Delete Snackbar
    LaunchedEffect(uiState.recentlyDeletedTask) {
        val deleted = uiState.recentlyDeletedTask
        if (deleted != null) {
            val result = snackbarHostState.showSnackbar(
                message = "Deleted \"${deleted.title}\"",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            } else {
                viewModel.clearRecentlyDeleted()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Subtle background glass glow gradients
        Box(
            modifier = Modifier
                .size(340.dp)
                .align(Alignment.TopStart)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CyanGlow.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.BottomEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            VioletGlow.copy(alpha = 0.07f),
                            Color.Transparent
                        )
                    )
                )
        )

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        taskToEdit = null
                        showAddTaskSheet = true
                    },
                    containerColor = Color.Transparent,
                    elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(0.dp),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(18.dp),
                            ambientColor = CyanGlow.copy(alpha = 0.5f),
                            spotColor = VioletGlow.copy(alpha = 0.5f)
                        )
                        .testTag("add_task_fab")
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                Brush.linearGradient(listOf(CyanGlow, VioletGlow)),
                                shape = RoundedCornerShape(18.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add new checklist item",
                            tint = Color(0xFF041E26),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Top App Header
                TopBarHeader(
                    themeMode = uiState.themeMode,
                    onToggleTheme = { viewModel.toggleThemeMode() },
                    isSearchExpanded = isSearchExpanded,
                    onToggleSearch = {
                        isSearchExpanded = !isSearchExpanded
                        if (!isSearchExpanded) viewModel.setSearchQuery("")
                    },
                    onOpenMenu = { showOptionsMenu = true }
                )

                // Expandable Search Bar
                AnimatedVisibility(
                    visible = isSearchExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Search checklist items...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = "Clear search",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanGlow,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_text_input")
                        )
                    }
                }

                // Checklists Horizontal Carousel
                ChecklistSelectorRow(
                    checklists = uiState.checklists,
                    selectedListId = uiState.selectedChecklistId,
                    onSelectList = { viewModel.selectChecklist(it) },
                    onAddNewList = { showCreateListDialog = true },
                    onDeleteList = { viewModel.deleteChecklist(it) }
                )

                // Main Tasks Content List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .testTag("tasks_lazy_column"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Progress & Filters Summary Card
                    item(key = "progress_card") {
                        ChecklistProgressCard(
                            totalTasks = uiState.totalCount,
                            completedTasks = uiState.completedCount,
                            filterMode = uiState.filterMode,
                            onFilterSelected = { viewModel.setFilterMode(it) },
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    // Section Heading & Sort Indicator
                    item(key = "section_header") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val countText = "${uiState.tasks.size} ${if (uiState.tasks.size == 1) "item" else "items"}"
                            Text(
                                text = countText.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                letterSpacing = 1.1.sp
                            )

                            Box {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { showSortMenu = true }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Sort,
                                        contentDescription = "Sort options",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = when (uiState.sortOption) {
                                            SortOption.DEFAULT -> "Default"
                                            SortOption.PRIORITY -> "Priority"
                                            SortOption.DUE_DATE -> "Due Date"
                                            SortOption.TITLE -> "Title"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                DropdownMenu(
                                    expanded = showSortMenu,
                                    onDismissRequest = { showSortMenu = false }
                                ) {
                                    SortOption.values().forEach { option ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = when (option) {
                                                        SortOption.DEFAULT -> "Default (Status)"
                                                        SortOption.PRIORITY -> "Priority (High to Low)"
                                                        SortOption.DUE_DATE -> "Due Date"
                                                        SortOption.TITLE -> "Alphabetical"
                                                    }
                                                )
                                            },
                                            onClick = {
                                                viewModel.setSortOption(option)
                                                showSortMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Empty State
                    if (uiState.tasks.isEmpty()) {
                        item(key = "empty_state") {
                            EmptyTasksPlaceholder(
                                filterMode = uiState.filterMode,
                                hasSearch = uiState.searchQuery.isNotBlank()
                            )
                        }
                    } else {
                        // Animated Task Items
                        items(
                            items = uiState.tasks,
                            key = { it.id }
                        ) { task ->
                            TaskItemRow(
                                task = task,
                                onToggleDone = { viewModel.toggleTask(it) },
                                onEditTask = {
                                    taskToEdit = it
                                    showAddTaskSheet = true
                                },
                                onDeleteTask = { viewModel.deleteTask(it) }
                            )
                        }
                    }
                }
            }

            // Top Menu Dropdown
            DropdownMenu(
                expanded = showOptionsMenu,
                onDismissRequest = { showOptionsMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Mark All as Done") },
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.DoneAll,
                            contentDescription = null,
                            tint = CyanGlow
                        )
                    },
                    onClick = {
                        viewModel.setAllDone(true)
                        showOptionsMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Mark All as To Do") },
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.RemoveDone,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        viewModel.setAllDone(false)
                        showOptionsMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Clear Completed") },
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.ClearAll,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        viewModel.clearCompleted()
                        showOptionsMenu = false
                    }
                )
            }
        }

        // Celebration Confetti
        CelebrationConfetti(
            triggerTime = uiState.celebrationTimestamp,
            modifier = Modifier.fillMaxSize()
        )

        // Add / Edit Task Modal Bottom Sheet
        if (showAddTaskSheet) {
            AddEditTaskBottomSheet(
                taskToEdit = taskToEdit,
                checklists = uiState.checklists,
                selectedChecklistId = uiState.selectedChecklistId,
                onDismiss = { showAddTaskSheet = false },
                onSaveTask = { title, desc, listId, priority, dueMillis ->
                    if (taskToEdit == null) {
                        viewModel.addTask(title, desc, listId, priority, dueMillis)
                    } else {
                        viewModel.updateTask(taskToEdit!!, title, desc, listId, priority, dueMillis)
                    }
                    showAddTaskSheet = false
                }
            )
        }

        // Create Checklist Dialog
        if (showCreateListDialog) {
            CreateListDialog(
                onDismiss = { showCreateListDialog = false },
                onCreateList = { name, iconName, colorHex ->
                    viewModel.createChecklist(name, iconName, colorHex)
                    showCreateListDialog = false
                }
            )
        }
    }
}

@Composable
private fun TopBarHeader(
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    isSearchExpanded: Boolean,
    onToggleSearch: () -> Unit,
    onOpenMenu: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title with Glass Glow Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(listOf(CyanGlow, VioletGlow))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.TaskAlt,
                    contentDescription = "CheckFlow",
                    tint = Color(0xFF041E26),
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = "CheckFlow",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Luminous Checklist",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Header Actions
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Search button
            IconButton(
                onClick = onToggleSearch,
                modifier = Modifier.testTag("toggle_search_button")
            ) {
                Icon(
                    imageVector = if (isSearchExpanded) Icons.Rounded.Close else Icons.Rounded.Search,
                    contentDescription = "Toggle search",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Dark/Light Theme toggle
            IconButton(
                onClick = onToggleTheme,
                modifier = Modifier.testTag("toggle_theme_button")
            ) {
                Icon(
                    imageVector = when (themeMode) {
                        ThemeMode.DARK -> Icons.Rounded.LightMode
                        ThemeMode.LIGHT -> Icons.Rounded.DarkMode
                        ThemeMode.SYSTEM -> Icons.Rounded.DarkMode
                    },
                    contentDescription = "Toggle dark mode",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Overflow Menu
            IconButton(
                onClick = onOpenMenu,
                modifier = Modifier.testTag("overflow_menu_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun ChecklistSelectorRow(
    checklists: List<ChecklistEntity>,
    selectedListId: Long?,
    onSelectList: (Long?) -> Unit,
    onAddNewList: () -> Unit,
    onDeleteList: (ChecklistEntity) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "All Tasks" Tab
        item(key = "all_lists_tab") {
            val isSelected = selectedListId == null
            GlassPill(
                title = "All Lists",
                icon = Icons.Rounded.DoneAll,
                isSelected = isSelected,
                accentColor = CyanGlow,
                onClick = { onSelectList(null) },
                testTag = "pill_all_lists"
            )
        }

        // Custom checklists
        items(
            items = checklists,
            key = { it.id }
        ) { list ->
            val isSelected = selectedListId == list.id
            val accentColor = Color(list.colorHex)
            GlassPill(
                title = list.name,
                icon = getChecklistIcon(list.iconName),
                isSelected = isSelected,
                accentColor = accentColor,
                onClick = { onSelectList(list.id) },
                onLongClick = { onDeleteList(list) },
                testTag = "pill_list_${list.id}"
            )
        }

        // "+ New List" Button
        item(key = "add_list_button") {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable(onClick = onAddNewList)
                    .padding(horizontal = 12.dp, vertical = 9.dp)
                    .testTag("add_list_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "New list",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "New List",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassPill(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    testTag: String
) {
    val bgModifier = if (isSelected) {
        Modifier.background(accentColor.copy(alpha = 0.22f))
    } else {
        Modifier.background(MaterialTheme.colorScheme.surface)
    }

    val borderModifier = if (isSelected) {
        Modifier.border(1.5.dp, accentColor, RoundedCornerShape(14.dp))
    } else {
        Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .then(bgModifier)
            .then(borderModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyTasksPlaceholder(
    filterMode: FilterMode,
    hasSearch: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                .border(1.dp, CyanGlow.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when {
                    hasSearch -> Icons.Rounded.Search
                    filterMode == FilterMode.DONE -> Icons.Rounded.CheckCircle
                    else -> Icons.Rounded.TaskAlt
                },
                contentDescription = null,
                tint = CyanGlow,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val title = when {
            hasSearch -> "No matching checklist items"
            filterMode == FilterMode.DONE -> "No completed items yet"
            filterMode == FilterMode.NEED_TO_DO -> "Nothing left to do! 🎉"
            else -> "Your checklist is empty"
        }

        val subtitle = when {
            hasSearch -> "Try adjusting your search terms"
            filterMode == FilterMode.DONE -> "Complete some tasks to view them here"
            filterMode == FilterMode.NEED_TO_DO -> "Awesome job! You finished all your items"
            else -> "Tap the + button below to add your first task"
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

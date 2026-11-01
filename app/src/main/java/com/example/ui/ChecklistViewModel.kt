package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChecklistEntity
import com.example.data.local.TaskEntity
import com.example.data.repository.TaskRepository
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FilterMode {
    ALL, NEED_TO_DO, DONE
}

enum class SortOption {
    DEFAULT, PRIORITY, DUE_DATE, TITLE
}

data class ChecklistUiState(
    val checklists: List<ChecklistEntity> = emptyList(),
    val selectedChecklistId: Long? = null,
    val tasks: List<TaskEntity> = emptyList(),
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val filterMode: FilterMode = FilterMode.ALL,
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.DEFAULT,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val celebrationTimestamp: Long = 0L,
    val recentlyDeletedTask: TaskEntity? = null
)

class ChecklistViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _selectedChecklistId = MutableStateFlow<Long?>(null)
    val selectedChecklistId: StateFlow<Long?> = _selectedChecklistId.asStateFlow()

    private val _filterMode = MutableStateFlow(FilterMode.ALL)
    val filterMode: StateFlow<FilterMode> = _filterMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.DEFAULT)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _celebrationTimestamp = MutableStateFlow(0L)
    val celebrationTimestamp: StateFlow<Long> = _celebrationTimestamp.asStateFlow()

    private val _recentlyDeletedTask = MutableStateFlow<TaskEntity?>(null)
    val recentlyDeletedTask: StateFlow<TaskEntity?> = _recentlyDeletedTask.asStateFlow()

    val uiState: StateFlow<ChecklistUiState> = combine(
        repository.allChecklists,
        repository.allTasks,
        _selectedChecklistId,
        _filterMode,
        _searchQuery,
        _sortOption,
        _themeMode,
        _celebrationTimestamp,
        _recentlyDeletedTask
    ) { params ->
        @Suppress("UNCHECKED_CAST")
        val checklists = params[0] as List<ChecklistEntity>
        @Suppress("UNCHECKED_CAST")
        val allTasks = params[1] as List<TaskEntity>
        val selectedId = params[2] as Long?
        val filter = params[3] as FilterMode
        val query = params[4] as String
        val sort = params[5] as SortOption
        val theme = params[6] as ThemeMode
        val celebration = params[7] as Long
        val deleted = params[8] as TaskEntity?

        // Filter by selected checklist
        val listTasks = if (selectedId == null) {
            allTasks
        } else {
            allTasks.filter { it.checklistId == selectedId }
        }

        val totalCount = listTasks.size
        val completedCount = listTasks.count { it.isDone }

        // Filter by status (All, Need to Do, Done)
        var filtered = when (filter) {
            FilterMode.ALL -> listTasks
            FilterMode.NEED_TO_DO -> listTasks.filter { !it.isDone }
            FilterMode.DONE -> listTasks.filter { it.isDone }
        }

        // Filter by search query
        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
            }
        }

        // Sort tasks
        val sorted = when (sort) {
            SortOption.DEFAULT -> filtered.sortedWith(
                compareBy<TaskEntity> { it.isDone }
                    .thenBy { it.orderIndex }
                    .thenByDescending { it.createdAt }
            )
            SortOption.PRIORITY -> filtered.sortedWith(
                compareBy<TaskEntity> { it.isDone }
                    .thenByDescending {
                        when (it.priority) {
                            "HIGH" -> 3
                            "MEDIUM" -> 2
                            else -> 1
                        }
                    }
            )
            SortOption.DUE_DATE -> filtered.sortedWith(
                compareBy<TaskEntity> { it.isDone }
                    .thenBy { it.dueDateMillis ?: Long.MAX_VALUE }
            )
            SortOption.TITLE -> filtered.sortedWith(
                compareBy<TaskEntity> { it.isDone }
                    .thenBy { it.title.lowercase() }
            )
        }

        ChecklistUiState(
            checklists = checklists,
            selectedChecklistId = selectedId,
            tasks = sorted,
            totalCount = totalCount,
            completedCount = completedCount,
            filterMode = filter,
            searchQuery = query,
            sortOption = sort,
            themeMode = theme,
            celebrationTimestamp = celebration,
            recentlyDeletedTask = deleted
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChecklistUiState()
    )

    fun selectChecklist(id: Long?) {
        _selectedChecklistId.value = id
    }

    fun setFilterMode(mode: FilterMode) {
        _filterMode.value = mode
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun toggleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            ThemeMode.SYSTEM -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.SYSTEM
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            val newDone = !task.isDone
            repository.setTaskDone(task.id, newDone)
            if (newDone) {
                _celebrationTimestamp.value = System.currentTimeMillis()
            }
        }
    }

    fun addTask(
        title: String,
        description: String,
        checklistId: Long,
        priority: String,
        dueDateMillis: Long?
    ) {
        viewModelScope.launch {
            val task = TaskEntity(
                checklistId = checklistId,
                title = title,
                description = description,
                priority = priority,
                dueDateMillis = dueDateMillis,
                isDone = false,
                createdAt = System.currentTimeMillis()
            )
            repository.insertTask(task)
        }
    }

    fun updateTask(
        task: TaskEntity,
        title: String,
        description: String,
        checklistId: Long,
        priority: String,
        dueDateMillis: Long?
    ) {
        viewModelScope.launch {
            repository.updateTask(
                task.copy(
                    title = title,
                    description = description,
                    checklistId = checklistId,
                    priority = priority,
                    dueDateMillis = dueDateMillis
                )
            )
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            _recentlyDeletedTask.value = task
            repository.deleteTask(task)
        }
    }

    fun undoDelete() {
        val taskToRestore = _recentlyDeletedTask.value ?: return
        viewModelScope.launch {
            repository.insertTask(taskToRestore)
            _recentlyDeletedTask.value = null
        }
    }

    fun clearRecentlyDeleted() {
        _recentlyDeletedTask.value = null
    }

    fun createChecklist(name: String, iconName: String, colorHex: Long) {
        viewModelScope.launch {
            val id = repository.insertChecklist(
                ChecklistEntity(
                    name = name,
                    iconName = iconName,
                    colorHex = colorHex
                )
            )
            _selectedChecklistId.value = id
        }
    }

    fun deleteChecklist(checklist: ChecklistEntity) {
        viewModelScope.launch {
            if (_selectedChecklistId.value == checklist.id) {
                _selectedChecklistId.value = null
            }
            repository.deleteChecklist(checklist)
        }
    }

    fun clearCompleted() {
        viewModelScope.launch {
            repository.clearCompleted(_selectedChecklistId.value)
        }
    }

    fun setAllDone(isDone: Boolean) {
        viewModelScope.launch {
            repository.setAllDone(_selectedChecklistId.value, isDone)
            if (isDone) {
                _celebrationTimestamp.value = System.currentTimeMillis()
            }
        }
    }

    class Factory(private val repository: TaskRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChecklistViewModel::class.java)) {
                return ChecklistViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

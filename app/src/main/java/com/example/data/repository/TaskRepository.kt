package com.example.data.repository

import com.example.data.local.ChecklistDao
import com.example.data.local.ChecklistEntity
import com.example.data.local.TaskDao
import com.example.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val checklistDao: ChecklistDao,
    private val taskDao: TaskDao
) {
    val allChecklists: Flow<List<ChecklistEntity>> = checklistDao.getAllChecklists()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTasksForChecklist(checklistId: Long): Flow<List<TaskEntity>> {
        return taskDao.getTasksForChecklist(checklistId)
    }

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun setTaskDone(taskId: Long, isDone: Boolean) {
        val completedAt = if (isDone) System.currentTimeMillis() else null
        taskDao.setTaskDone(taskId, isDone, completedAt)
    }

    suspend fun insertChecklist(checklist: ChecklistEntity): Long {
        return checklistDao.insertChecklist(checklist)
    }

    suspend fun updateChecklist(checklist: ChecklistEntity) {
        checklistDao.updateChecklist(checklist)
    }

    suspend fun deleteChecklist(checklist: ChecklistEntity) {
        taskDao.deleteTasksForChecklist(checklist.id)
        checklistDao.deleteChecklist(checklist)
    }

    suspend fun clearCompleted(checklistId: Long?) {
        if (checklistId == null) {
            taskDao.deleteCompletedTasks()
        } else {
            taskDao.deleteCompletedTasksForChecklist(checklistId)
        }
    }

    suspend fun setAllDone(checklistId: Long?, isDone: Boolean) {
        val completedAt = if (isDone) System.currentTimeMillis() else null
        if (checklistId == null) {
            taskDao.setAllDone(isDone, completedAt)
        } else {
            taskDao.setAllDoneForChecklist(checklistId, isDone, completedAt)
        }
    }
}

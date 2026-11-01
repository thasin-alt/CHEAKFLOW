package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isDone ASC, orderIndex ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE checklistId = :checklistId ORDER BY isDone ASC, orderIndex ASC, createdAt DESC")
    fun getTasksForChecklist(checklistId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("UPDATE tasks SET isDone = :isDone, completedAt = :completedAt WHERE id = :id")
    suspend fun setTaskDone(id: Long, isDone: Boolean, completedAt: Long?)

    @Query("DELETE FROM tasks WHERE isDone = 1")
    suspend fun deleteCompletedTasks()

    @Query("DELETE FROM tasks WHERE isDone = 1 AND checklistId = :checklistId")
    suspend fun deleteCompletedTasksForChecklist(checklistId: Long)

    @Query("UPDATE tasks SET isDone = :isDone, completedAt = :completedAt")
    suspend fun setAllDone(isDone: Boolean, completedAt: Long?)

    @Query("UPDATE tasks SET isDone = :isDone, completedAt = :completedAt WHERE checklistId = :checklistId")
    suspend fun setAllDoneForChecklist(checklistId: Long, isDone: Boolean, completedAt: Long?)

    @Query("DELETE FROM tasks WHERE checklistId = :checklistId")
    suspend fun deleteTasksForChecklist(checklistId: Long)

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getCount(): Int
}

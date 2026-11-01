package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    indices = [Index(value = ["checklistId"])]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val checklistId: Long = 1L,
    val title: String,
    val description: String = "",
    val isDone: Boolean = false,
    val priority: String = "MEDIUM", // "LOW", "MEDIUM", "HIGH"
    val dueDateMillis: Long? = null,
    val completedAt: Long? = null,
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklists")
data class ChecklistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String = "checklist",
    val colorHex: Long = 0xFF00F0FF,
    val createdAt: Long = System.currentTimeMillis()
)

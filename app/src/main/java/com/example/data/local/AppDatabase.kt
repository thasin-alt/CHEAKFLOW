package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ChecklistEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun checklistDao(): ChecklistDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "checkflow_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.checklistDao(), database.taskDao())
                }
            }
        }

        private suspend fun populateInitialData(checklistDao: ChecklistDao, taskDao: TaskDao) {
            val personalListId = checklistDao.insertChecklist(
                ChecklistEntity(
                    name = "Personal",
                    iconName = "star",
                    colorHex = 0xFF00F0FF
                )
            )
            val workListId = checklistDao.insertChecklist(
                ChecklistEntity(
                    name = "Work & Study",
                    iconName = "work",
                    colorHex = 0xFF8B5CF6
                )
            )
            val shoppingListId = checklistDao.insertChecklist(
                ChecklistEntity(
                    name = "Shopping",
                    iconName = "shopping",
                    colorHex = 0xFF10B981
                )
            )

            // Seed starter tasks demonstrating features
            taskDao.insertTasks(
                listOf(
                    TaskEntity(
                        checklistId = personalListId,
                        title = "Welcome to CheckFlow! ✨",
                        description = "Tap the glowing circle to mark tasks as done and enjoy the smooth celebration animation.",
                        isDone = false,
                        priority = "HIGH"
                    ),
                    TaskEntity(
                        checklistId = personalListId,
                        title = "Try switching between lists",
                        description = "Use the glass pill tabs above to organize Personal, Work, or Shopping tasks.",
                        isDone = false,
                        priority = "MEDIUM"
                    ),
                    TaskEntity(
                        checklistId = personalListId,
                        title = "Swipe or tap to edit tasks",
                        description = "Swipe left to delete or tap any task to view and customize details.",
                        isDone = true,
                        priority = "LOW",
                        completedAt = System.currentTimeMillis() - 3600000L
                    ),
                    TaskEntity(
                        checklistId = workListId,
                        title = "Prepare sprint review presentation",
                        description = "Include metrics, quarterly checklist achievements, and upcoming roadmap.",
                        isDone = false,
                        priority = "HIGH"
                    ),
                    TaskEntity(
                        checklistId = shoppingListId,
                        title = "Organic matcha green tea",
                        description = "Ceremonial grade from local market.",
                        isDone = false,
                        priority = "MEDIUM"
                    )
                )
            )
        }
    }
}

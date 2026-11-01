package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ChecklistEntity
import com.example.data.local.TaskEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CheckFlow", appName)
    }

    @Test
    fun `task entity initial state and completion toggle`() {
        val task = TaskEntity(
            id = 1L,
            checklistId = 1L,
            title = "Test Checklist Item",
            description = "Test Description",
            isDone = false,
            priority = "HIGH"
        )
        assertFalse(task.isDone)
        assertEquals("HIGH", task.priority)

        val completedTask = task.copy(isDone = true, completedAt = 123456L)
        assertTrue(completedTask.isDone)
        assertEquals(123456L, completedTask.completedAt)
    }

    @Test
    fun `checklist entity properties`() {
        val checklist = ChecklistEntity(
            id = 10L,
            name = "Work Projects",
            iconName = "work",
            colorHex = 0xFF8B5CF6
        )
        assertEquals("Work Projects", checklist.name)
        assertEquals("work", checklist.iconName)
    }
}

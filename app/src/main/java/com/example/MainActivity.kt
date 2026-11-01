package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AppDatabase
import com.example.data.repository.TaskRepository
import com.example.ui.ChecklistScreen
import com.example.ui.ChecklistViewModel
import com.example.ui.theme.CheckFlowTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ChecklistViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = TaskRepository(database.checklistDao(), database.taskDao())
        ChecklistViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            CheckFlowTheme(themeMode = uiState.themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ChecklistScreen(viewModel = viewModel)
                }
            }
        }
    }
}

package com.quantavil.quicklens.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quantavil.quicklens.QuickLensApplication
import com.quantavil.quicklens.data.SearchHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as QuickLensApplication).database.searchHistoryDao()
    private val uiPreferences = com.quantavil.quicklens.utils.UIPreferences(application)
    
    val history: Flow<List<SearchHistory>> = dao.getAllHistory()
    
    init {
        // Auto-cleanup old history on ViewModel creation
        cleanupOldHistory()
    }
    
    private fun cleanupOldHistory() {
        val retentionDays = uiPreferences.getHistoryRetentionDays()
        if (retentionDays > 0) { // -1 means never clear
            val cutoffTimestamp = System.currentTimeMillis() - (retentionDays * 24 * 60 * 60 * 1000L)
            viewModelScope.launch {
                dao.deleteOlderThan(cutoffTimestamp)
            }
        }
    }

    fun delete(item: SearchHistory) {
        viewModelScope.launch {
            // Delete file
            try {
                val file = File(item.thumbnailPath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dao.delete(item)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }
}

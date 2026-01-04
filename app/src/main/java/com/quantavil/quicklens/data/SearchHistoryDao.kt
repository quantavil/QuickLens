package com.quantavil.quicklens.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<SearchHistory>>

    @Insert
    suspend fun insert(history: SearchHistory)

    @Delete
    suspend fun delete(history: SearchHistory)
    
    @Query("DELETE FROM search_history")
    suspend fun clearAll()
    
    @Query("DELETE FROM search_history WHERE timestamp < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)
}

package com.quantavil.quicklens.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val thumbnailPath: String, // Path to local bitmap file
    val originalImageUrl: String? // URL if it was uploaded/hosted
)

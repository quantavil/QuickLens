package com.quantavil.quicklens.utils

import android.graphics.Bitmap
import android.graphics.Rect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageUtils {

    suspend fun cropBitmap(source: Bitmap, rect: Rect): Bitmap = withContext(Dispatchers.Default) {
        // Ensure rect is within bounds
        val left = rect.left.coerceIn(0, source.width)
        val top = rect.top.coerceIn(0, source.height)
        val width = rect.width().coerceAtMost(source.width - left)
        val height = rect.height().coerceAtMost(source.height - top)
        
        if (width > 0 && height > 0) {
            Bitmap.createBitmap(source, left, top, width, height)
        } else {
            source // Fallback
        }
    }

    suspend fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap = withContext(Dispatchers.Default) {
        try {
            if (source.width <= maxLength && source.height <= maxLength) return@withContext source
            val aspectRatio = source.width.toDouble() / source.height.toDouble()
            val targetWidth = if (aspectRatio >= 1) maxLength else (maxLength * aspectRatio).toInt()
            val targetHeight = if (aspectRatio < 1) maxLength else (maxLength / aspectRatio).toInt()
            Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
        } catch (e: Exception) {
            source
        }
    }
}

package com.quantavil.quicklens.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

object BitmapRepository {
    private const val FILE_NAME = "screenshot_cache.jpg"
    
    fun saveBitmap(context: Context, bitmap: Bitmap) {
        try {
            val file = File(context.cacheDir, FILE_NAME)
            FileOutputStream(file).use { out ->
                // Use JPEG 85% for faster I/O and smaller file size
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadBitmap(context: Context): Bitmap? {
        val file = File(context.cacheDir, FILE_NAME)
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
    }

    fun clear(context: Context) {
        val file = File(context.cacheDir, FILE_NAME)
        if (file.exists()) {
            file.delete()
        }
    }
}

package com.quantavil.quicklens.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quantavil.quicklens.QuickLensApplication
import com.quantavil.quicklens.data.SearchEngine
import com.quantavil.quicklens.data.SearchHistory
import com.quantavil.quicklens.utils.ImageSearchUploader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.util.UUID

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val historyDao = (application as QuickLensApplication).database.searchHistoryDao()

    private val _hostedImageUrl = MutableStateFlow<String?>(null)
    val hostedImageUrl: StateFlow<String?> = _hostedImageUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedEngine = MutableStateFlow(SearchEngine.BING)
    val selectedEngine: StateFlow<SearchEngine> = _selectedEngine.asStateFlow()
    
    private val _searchUrls = MutableStateFlow<Map<SearchEngine, String>>(emptyMap())
    val searchUrls: StateFlow<Map<SearchEngine, String>> = _searchUrls.asStateFlow()

    // Keep reference for retry
    private var lastBitmap: Bitmap? = null

    fun onImageCropped(bitmap: Bitmap, context: Context?) {
        lastBitmap = bitmap
        performUpload(bitmap)
    }

    fun retryLastRequest() {
        lastBitmap?.let {
            performUpload(it)
        }
    }

    private fun performUpload(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _errorMessage.value = null
            _searchUrls.value = emptyMap()
            
            try {
                val imageUrl = ImageSearchUploader.uploadImage(bitmap)
                if (imageUrl != null) {
                    _hostedImageUrl.value = imageUrl
                    
                    // Save to history
                    saveToHistory(bitmap, imageUrl)

                    val initialUrls = mutableMapOf<SearchEngine, String>()
                    
                    // 1. Load Primary (Bing) Immediately
                    val primaryEngine = SearchEngine.BING
                    generateUrlForEngine(imageUrl, primaryEngine)?.let { url ->
                        initialUrls[primaryEngine] = url
                        _searchUrls.value = initialUrls.toMap() // Emit immediate update
                    }

                    // 2. Load Background Engines after delay
                    delay(800)
                    
                    val backgroundEngines = listOf(
                        SearchEngine.GOOGLE_LENS,
                        SearchEngine.YANDEX
                    )

                    backgroundEngines.forEach { engine ->
                         generateUrlForEngine(imageUrl, engine)?.let { url ->
                             initialUrls[engine] = url
                         }
                    }
                    _searchUrls.value = initialUrls.toMap()

                } else {
                    _errorMessage.value = "Failed to upload image. Please check your connection."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "An error occurred: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun saveToHistory(bitmap: Bitmap, imageUrl: String) {
        try {
            // Save thumbnail to internal storage
            val filename = "thumb_${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
            val file = File(getApplication<Application>().filesDir, filename)
            FileOutputStream(file).use { out ->
                // Save a smaller thumbnail
                val thumb = Bitmap.createScaledBitmap(bitmap, 200, (200 * bitmap.height / bitmap.width), true)
                thumb.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }

            val history = SearchHistory(
                timestamp = System.currentTimeMillis(),
                thumbnailPath = file.absolutePath,
                originalImageUrl = imageUrl
            )
            historyDao.insert(history)
        } catch (e: Exception) {
            e.printStackTrace()
            // Don't fail the whole flow if history save fails
        }
    }

    fun onEngineSelected(engine: SearchEngine) {
        _selectedEngine.value = engine
        
        val currentUrls = _searchUrls.value
        val hostedImage = _hostedImageUrl.value
        
        if (!currentUrls.containsKey(engine) && hostedImage != null) {
             generateUrlForEngine(hostedImage, engine)?.let { url ->
                 _searchUrls.value = currentUrls + (engine to url)
             }
        }
    }

    private fun generateUrlForEngine(imageUrl: String, engine: SearchEngine): String? {
        return try {
            val encodedUrl = URLEncoder.encode(imageUrl, "UTF-8")
            engine.urlTemplate.replace("{imageUrl}", encodedUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun restoreSearch(imageUrl: String) {
        _hostedImageUrl.value = imageUrl
        
        val initialUrls = mutableMapOf<SearchEngine, String>()
        
        // Use a coroutine for staggered restore as well
        viewModelScope.launch {
            // 1. Load Primary (Bing) Immediately
            val primaryEngine = SearchEngine.BING
            generateUrlForEngine(imageUrl, primaryEngine)?.let { url ->
                initialUrls[primaryEngine] = url
                _searchUrls.value = initialUrls.toMap()
            }

            // 2. Load Background Engines after delay
            delay(800)

            val backgroundEngines = listOf(
                SearchEngine.GOOGLE_LENS,
                SearchEngine.YANDEX
            )

            backgroundEngines.forEach { engine ->
                 generateUrlForEngine(imageUrl, engine)?.let { url ->
                     initialUrls[engine] = url
                 }
            }
            _searchUrls.value = initialUrls.toMap()
        }
    }
}

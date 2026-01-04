package com.quantavil.quicklens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.quantavil.quicklens.data.BitmapRepository
import com.quantavil.quicklens.ui.QuickLensScreen
import com.quantavil.quicklens.ui.theme.QuickLensTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OverlayActivity : ComponentActivity() {
    
    private val screenshotBitmap = androidx.compose.runtime.mutableStateOf<android.graphics.Bitmap?>(null)
    private val isLoading = androidx.compose.runtime.mutableStateOf(true)
    private val initialUrl = androidx.compose.runtime.mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        handleIntent(intent)
        loadScreenshot()

        setContent {
            val uiPreferences = com.quantavil.quicklens.utils.UIPreferences(this)
            val appThemeMode = uiPreferences.getThemeMode()
            
            QuickLensTheme(appThemeMode = appThemeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    if (!isLoading.value || initialUrl.value != null) {
                        QuickLensScreen(
                            screenshot = screenshotBitmap.value,
                            initialUrl = initialUrl.value,
                            onClose = { 
                                BitmapRepository.clear(this@OverlayActivity)
                                finish() 
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
        loadScreenshot()
    }

    private fun handleIntent(intent: android.content.Intent) {
        if (intent.hasExtra("EXTRA_INITIAL_SEARCH_URL")) {
            initialUrl.value = intent.getStringExtra("EXTRA_INITIAL_SEARCH_URL")
        }
    }

    private fun loadScreenshot() {
        isLoading.value = true
        // Launch coroutine to load bitmap
        lifecycleScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val bitmap = BitmapRepository.loadBitmap(this@OverlayActivity)
            withContext(kotlinx.coroutines.Dispatchers.Main) {
                if (bitmap != null) {
                    // Recycle old bitmap if it exists to prevent memory leak
                    screenshotBitmap.value?.recycle()
                    screenshotBitmap.value = bitmap
                }
                isLoading.value = false
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Recycle screenshot bitmap to prevent memory leak
        screenshotBitmap.value?.recycle()
        screenshotBitmap.value = null
        // Notify accessibility service that overlay is closed
        QuickLensAccessibilityService.onOverlayClosed()
        if (isFinishing) {
             BitmapRepository.clear(this)
        }
    }
}

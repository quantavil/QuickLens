package com.quantavil.quicklens.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.quantavil.quicklens.data.SearchEngine
import com.quantavil.quicklens.ui.SearchViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import com.quantavil.quicklens.ui.theme.AppIcons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.DisposableEffect

@Composable
fun ResultSheet(
    viewModel: SearchViewModel,
    isExpanded: Boolean,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    openLinksExternally: Boolean = false,
    isDesktopMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val searchEngines = SearchEngine.entries.toTypedArray()
    val selectedEngine by viewModel.selectedEngine.collectAsState()
    val searchUrls by viewModel.searchUrls.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    
    // Derived state for share/copy
    val currentUrl = searchUrls[selectedEngine]
    
    // We need to track if we can go back in the current WebView to intercept back presses
    var canGoBack by remember { mutableStateOf(false) }
    // We need a way to trigger goBack on the active WebView
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    
    // Track all WebView instances for proper cleanup
    val webViewInstances = remember { mutableMapOf<SearchEngine, WebView>() }
    
    // Cleanup WebViews when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            webViewInstances.values.forEach { webView ->
                try {
                    webView.stopLoading()
                    webView.loadUrl("about:blank")
                    webView.clearHistory()
                    webView.clearCache(true)
                    webView.clearFormData()
                    (webView.parent as? ViewGroup)?.removeView(webView)
                    webView.removeAllViews()
                    webView.destroy()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            webViewInstances.clear()
        }
    }
    
    // Intercept Back Press
    BackHandler(enabled = isExpanded && canGoBack) {
        webViewRef?.goBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Header with Actions
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
             ScrollableTabRow(
                selectedTabIndex = searchEngines.indexOf(selectedEngine),
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                divider = {},
                indicator = {},
                modifier = Modifier.weight(1f)
            ) {
                searchEngines.forEach { engine ->
                    val selected = selectedEngine == engine
                    val transition = updateTransition(targetState = selected, label = "TabSelect")
                    val scale by transition.animateFloat(label = "Scale") { if (it) 1.05f else 1f }
                    val alpha by transition.animateFloat(label = "Alpha") { if (it) 1f else 0.7f }

                    Tab(
                        selected = selected,
                        onClick = { 
                            viewModel.onEngineSelected(engine) 
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier.graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        },
                        text = {
                            Text(
                                engine.displayName,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                ),
                                modifier = Modifier
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
            
            // Action Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    currentUrl?.let { url ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Search Result", url)
                        clipboard.setPrimaryClip(clip)
                        android.widget.Toast.makeText(context, "Link copied", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }, enabled = currentUrl != null) {
                    Icon(AppIcons.ContentCopy, contentDescription = "Copy Link", tint = MaterialTheme.colorScheme.primary)
                }
                
                IconButton(onClick = {
                    currentUrl?.let { url ->
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, url)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Search Result"))
                    }
                }, enabled = currentUrl != null) {
                   Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            
            // Error State with Retry
            val errorMessage by viewModel.errorMessage.collectAsState()
            if (errorMessage != null) {
                 Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        AppIcons.ErrorOutline, 
                        contentDescription = null, 
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.retryLastRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retry")
                    }
                }
            }
            
            // Render a WebView for EACH active URL. 
            // We Stack them and control Z-index/Visibility so they don't reload when switched.
            for ((engine, url) in searchUrls) {
                val isSelected = (selectedEngine == engine)
                
                // key() ensures each WebView is uniquely tracked by Compose.
                key(engine) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                alpha = if (isSelected) 1f else 0f
                            }
                            .zIndex(if (isSelected) 1f else 0f)
                    
                    ) {
                        var webViewProgress by remember { androidx.compose.runtime.mutableIntStateOf(0) }
                        
                        // Gesture State for visual feedback
                        var swipeDirection by remember { mutableStateOf<Int?>(null) } // -1 for Right (Back), 1 for Left (Forward)

                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )

                                    // Initialize GestureDetector for this WebView
                                    val gestureDetector = android.view.GestureDetector(ctx, object : android.view.GestureDetector.SimpleOnGestureListener() {
                                        private val SWIPE_THRESHOLD = 100
                                        private val SWIPE_VELOCITY_THRESHOLD = 100

                                        override fun onDown(e: android.view.MotionEvent): Boolean {
                                            return false // Let WebView handle clicks/scrolls
                                        }

                                        override fun onFling(
                                            e1: android.view.MotionEvent?,
                                            e2: android.view.MotionEvent,
                                            velocityX: Float,
                                            velocityY: Float
                                        ): Boolean {
                                            if (e1 == null) return false
                                            
                                            val diffY = e2.y - e1.y
                                            val diffX = e2.x - e1.x
                                            
                                            // Only trigger if horizontal swipe is dominant
                                            if (kotlin.math.abs(diffX) > kotlin.math.abs(diffY)) {
                                                if (kotlin.math.abs(diffX) > SWIPE_THRESHOLD && kotlin.math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                                                    if (diffX > 0) {
                                                        // Swipe Right -> Go Back
                                                        if (canGoBack()) {
                                                            goBack()
                                                            swipeDirection = -1
                                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            return true
                                                        }
                                                    } else {
                                                        // Swipe Left -> Go Forward
                                                        if (canGoForward()) {
                                                            goForward()
                                                            swipeDirection = 1
                                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            return true
                                                        }
                                                    }
                                                }
                                            }
                                            return false
                                        }
                                    })

                                    // Set OnTouchListener to pass events to GestureDetector
                                    setOnTouchListener { _, event ->
                                        gestureDetector.onTouchEvent(event)
                                        false // Always return false so WebView still receives the touch events
                                    }

                                    settings.apply {
                                        javaScriptEnabled = true
                                        domStorageEnabled = true
                                        cacheMode = WebSettings.LOAD_DEFAULT
                                        userAgentString = if (isDesktopMode) {
                                            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                                        } else {
                                            "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                                        }
                                        
                                        // Fix for Dark Mode
                                        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                                            WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, isDarkTheme)
                                        } else if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                                            @Suppress("DEPRECATION")
                                            if (isDarkTheme) {
                                                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_ON)
                                            } else {
                                                 WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_OFF)
                                            }
                                        }
                                    }
                                    
                                    webChromeClient = object : android.webkit.WebChromeClient() {
                                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                            webViewProgress = newProgress
                                        }
                                    }
                                    
                                    val initialUrl = url
                                    webViewClient = object : WebViewClient() {
                                        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                                            super.doUpdateVisitedHistory(view, url, isReload)
                                            if (isSelected) {
                                                canGoBack = view?.canGoBack() == true
                                                webViewRef = view
                                            }
                                        }

                                        override fun shouldOverrideUrlLoading(
                                            view: WebView?,
                                            request: WebResourceRequest?
                                        ): Boolean {
                                            val clickedUrl = request?.url?.toString() ?: return false
                                            
                                            // List of allowed domains (search engines)
                                            val allowedDomains = setOf(
                                                "lens.google.com",
                                                "google.com", 
                                                "bing.com", 
                                                "yandex.com", 
                                                "tineye.com"
                                            )
                                            
                                            val host = android.net.Uri.parse(clickedUrl).host?.lowercase()
                                            val isAllowed = host != null && allowedDomains.any { domain -> 
                                                host == domain || host.endsWith(".$domain")
                                            }
                                            
                                            // Only open externally if preference is enabled AND it's not a search engine
                                            if (openLinksExternally && !isAllowed) {
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(clickedUrl)).apply {
                                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    }
                                                    ctx.startActivity(intent)
                                                    return true // Open in external browser
                                                } catch (e: Exception) {
                                                    // Fallback
                                                }
                                            }
                                            return false // Load in WebView
                                        }
                                    }
                                    
                                    loadUrl(url)
                                    
                                    // Register WebView for cleanup tracking
                                    webViewInstances[engine] = this
                                }
                            },
                            update = { webView ->
                                if (isSelected) {
                                    webViewRef = webView
                                    canGoBack = webView.canGoBack()
                                }
                                
                                // Update Dark Mode dynamically
                                if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                                     WebSettingsCompat.setAlgorithmicDarkeningAllowed(webView.settings, isDarkTheme)
                                } else if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                                     @Suppress("DEPRECATION")
                                     if (isDarkTheme) {
                                         WebSettingsCompat.setForceDark(webView.settings, WebSettingsCompat.FORCE_DARK_ON)
                                     } else {
                                          WebSettingsCompat.setForceDark(webView.settings, WebSettingsCompat.FORCE_DARK_OFF)
                                     }
                                }

                                if (webView.url != url && webView.originalUrl != url) {
                                    webView.loadUrl(url)
                                }
                            },
                            onReset = { webView ->
                                // Proper WebView cleanup to prevent memory leaks
                                webView.stopLoading()
                                webView.loadUrl("about:blank")
                                webView.clearHistory()
                                webView.clearCache(true) // Clear disk cache to prevent buildup
                                webView.removeAllViews()
                                webView.destroy()
                            }
                        )
                            
                        // Visual Swipe Indicators - Small and at the edges
                        if (swipeDirection != null) {
                            Box(
                                modifier = Modifier
                                    .align(
                                        if (swipeDirection == -1) Alignment.CenterStart else Alignment.CenterEnd
                                    )
                                    .padding(horizontal = 8.dp)
                            ) {
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = true,
                                    enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInHorizontally {
                                        if (swipeDirection == -1) -it else it
                                    },
                                    exit = androidx.compose.animation.fadeOut()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (swipeDirection == -1) Icons.Default.ArrowBack else Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Reset swipe direction after a delay to hide indicator
                        if (swipeDirection != null) {
                            androidx.compose.runtime.LaunchedEffect(swipeDirection) {
                                kotlinx.coroutines.delay(1000)
                                swipeDirection = null
                            }
                        }
                        
                        // WebView Loading Indicator
                        if (webViewProgress < 100) {
                                androidx.compose.material3.LinearProgressIndicator(
                                progress = { webViewProgress / 100f },
                                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Color.Transparent
                            )
                        }
                    }
                }
            }
        }
    }
}

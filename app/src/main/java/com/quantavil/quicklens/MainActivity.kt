package com.quantavil.quicklens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.quantavil.quicklens.ui.theme.QuickLensTheme
import com.quantavil.quicklens.ui.theme.AppIcons
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiPreferences = remember { com.quantavil.quicklens.utils.UIPreferences(this) }
            val appThemeMode = uiPreferences.getThemeMode()
            
            QuickLensTheme(appThemeMode = appThemeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var currentScreen by remember { androidx.compose.runtime.mutableStateOf("setup") }
    
    if (currentScreen == "history") {
        com.quantavil.quicklens.ui.HistoryScreen(
            onBack = { currentScreen = "setup" }
        )
    } else {
        SetupScreen(
            onOpenHistory = { currentScreen = "history" }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(onOpenHistory: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    // Preferences
    val uiPreferences = remember { com.quantavil.quicklens.utils.UIPreferences(context) }
    var appThemeMode by remember { mutableStateOf(uiPreferences.getThemeMode()) }
    var isDesktopMode by remember { mutableStateOf(uiPreferences.isDesktopMode()) }

    var openLinksExternally by remember { mutableStateOf(uiPreferences.isOpenLinksExternally()) }
    var isBubbleEnabled by remember { mutableStateOf(uiPreferences.isBubbleEnabled()) }
    
    // Permission States
    var isAccessibilityEnabled by remember { mutableStateOf(isAccessibilityServiceEnabled(context)) }
    
    // Check permissions on Resume
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                isAccessibilityEnabled = isAccessibilityServiceEnabled(context)
                appThemeMode = uiPreferences.getThemeMode()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("") }, 
                actions = {
                    IconButton(onClick = onOpenHistory) {
                        Icon(AppIcons.History, contentDescription = "History", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // 1. Header
            Text(
                text = "QuickLens",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Search anything on your screen instantly.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // 2. REQUIRED: Accessibility Service
            Text(
                text = "REQUIRED",
                style = MaterialTheme.typography.labelSmall,
                color = if (isAccessibilityEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )
            
            if (isAccessibilityEnabled) {
                // Granted State
                Card(
                     colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AppIcons.CheckCircle,
                            contentDescription = "Granted",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Accessibility Service Active",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Double tap status bar to launch QuickLens",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            } else {
                 // Action Needed State
                Card(
                    onClick = {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AppIcons.Accessibility,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Enable Accessibility",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                             Text(
                                text = "To do its job properly, the app needs this permission.\n" +
                                        "Tap allow and we’re good to go! \uD83D\uDC4D",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 3. OPTIONAL: Default Assistant
            val componentName = android.content.ComponentName(context, QuickLensSessionService::class.java)
            val isDefaultAssistant = Settings.Secure.getString(context.contentResolver, "voice_interaction_service") == componentName.flattenToString()
            
            Text(
                text = "OPTIONAL",
                style = MaterialTheme.typography.labelSmall,
                color = if (isDefaultAssistant) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            Card(
                onClick = {
                    val intent = Intent(Settings.ACTION_VOICE_INPUT_SETTINGS)
                    context.startActivity(intent)
                },
                colors = CardDefaults.cardColors(
                    containerColor = if (isDefaultAssistant) 
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    else 
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isDefaultAssistant) AppIcons.CheckCircle else AppIcons.Assistant,
                        contentDescription = null,
                        tint = if (isDefaultAssistant) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isDefaultAssistant) "Default Assistant Active" else "Set as Default Assistant",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDefaultAssistant) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Long-press home checking to trigger QuickLens",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDefaultAssistant) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!isDefaultAssistant) {
                        Icon(
                            imageVector = AppIcons.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 4. Settings
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Theme Selector
                    Text(
                        text = "App Theme",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ThemeOption(
                            label = "System",
                            selected = appThemeMode == com.quantavil.quicklens.utils.UIPreferences.THEME_SYSTEM,
                            onClick = { 
                                appThemeMode = com.quantavil.quicklens.utils.UIPreferences.THEME_SYSTEM
                                uiPreferences.setThemeMode(appThemeMode)
                                (context as? ComponentActivity)?.recreate()
                            }
                        )
                        ThemeOption(
                            label = "Light",
                            selected = appThemeMode == com.quantavil.quicklens.utils.UIPreferences.THEME_LIGHT,
                            onClick = { 
                                appThemeMode = com.quantavil.quicklens.utils.UIPreferences.THEME_LIGHT
                                uiPreferences.setThemeMode(appThemeMode)
                                (context as? ComponentActivity)?.recreate()
                            }
                        )
                        ThemeOption(
                            label = "Dark",
                            selected = appThemeMode == com.quantavil.quicklens.utils.UIPreferences.THEME_DARK,
                            onClick = { 
                                appThemeMode = com.quantavil.quicklens.utils.UIPreferences.THEME_DARK
                                uiPreferences.setThemeMode(appThemeMode)
                                (context as? ComponentActivity)?.recreate()
                            }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    
                    // Desktop Mode
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                isDesktopMode = !isDesktopMode
                                uiPreferences.setDesktopMode(isDesktopMode)
                            }
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Desktop Mode",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Request desktop sites in search results",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isDesktopMode,
                            onCheckedChange = { 
                                isDesktopMode = it
                                uiPreferences.setDesktopMode(it)
                            }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))





                    // Open Links Externally
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                openLinksExternally = !openLinksExternally
                                uiPreferences.setOpenLinksExternally(openLinksExternally)
                            }
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Open Links Externally",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Open clicked links in your default browser",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = openLinksExternally,
                            onCheckedChange = { 
                                openLinksExternally = it
                                uiPreferences.setOpenLinksExternally(it)
                            }
                        )
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Floating Bubble
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                isBubbleEnabled = !isBubbleEnabled
                                uiPreferences.setBubbleEnabled(isBubbleEnabled)
                            }
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Floating Trigger Bubble",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Show a floating bubble to trigger search",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isBubbleEnabled,
                            onCheckedChange = { 
                                isBubbleEnabled = it
                                uiPreferences.setBubbleEnabled(it)
                            }
                        )
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    
                    // History Retention
                    var historyRetention by remember { mutableStateOf(uiPreferences.getHistoryRetentionDays()) }
                    var showRetentionMenu by remember { mutableStateOf(false) }
                    
                    val retentionOptions = listOf(
                        -1 to "Never",
                        1 to "1 day",
                        7 to "7 days",
                        15 to "15 days",
                        30 to "30 days"
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showRetentionMenu = true }
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-clear History",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Delete old history entries after",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box {
                            TextButton(onClick = { showRetentionMenu = true }) {
                                Text(retentionOptions.find { it.first == historyRetention }?.second ?: "Never")
                            }
                            DropdownMenu(
                                expanded = showRetentionMenu,
                                onDismissRequest = { showRetentionMenu = false }
                            ) {
                                retentionOptions.forEach { (days, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            historyRetention = days
                                            uiPreferences.setHistoryRetentionDays(days)
                                            showRetentionMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))



            Spacer(modifier = Modifier.height(32.dp)) 
        }
    }
}

@Composable
fun ThemeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (selected) {
            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
        } else null
    )
}

// Helper Functions
fun isAccessibilityServiceEnabled(context: android.content.Context): Boolean {
    val expectedComponentName = android.content.ComponentName(context, QuickLensAccessibilityService::class.java)
    val enabledServicesSetting = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false
    
    val colonSplitter = android.text.TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServicesSetting)
    
    while (colonSplitter.hasNext()) {
        val componentNameString = colonSplitter.next()
        val enabledComponent = android.content.ComponentName.unflattenFromString(componentNameString)
        if (enabledComponent != null && enabledComponent == expectedComponentName)
            return true
    }
    return false
}

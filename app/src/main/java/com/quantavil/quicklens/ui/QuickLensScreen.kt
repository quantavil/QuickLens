package com.quantavil.quicklens.ui

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quantavil.quicklens.ui.components.CroppingOverlay
import com.quantavil.quicklens.ui.components.ResultSheet
import com.quantavil.quicklens.utils.UIPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLensScreen(
    screenshot: Bitmap?,
    initialUrl: String? = null,
    onClose: () -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val uiPreferences = remember { UIPreferences(context) }


    // Restore search if initialUrl is provided
    LaunchedEffect(initialUrl) {
        if (initialUrl != null) {
            viewModel.restoreSearch(initialUrl)
        }
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = if (initialUrl != null) SheetValue.Expanded else SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    // Error & Loading States
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            scaffoldState.bottomSheetState.expand()
        }
    }

    BackHandler(enabled = true) {
        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
        } else {
            onClose()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = (LocalConfiguration.current.screenHeightDp.dp * 0.55f),
        sheetContainerColor = Color.Transparent,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetSwipeEnabled = scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded,
        sheetContent = {
             Box(modifier = Modifier.fillMaxSize()) {
                 val isDarkTheme = when (uiPreferences.getThemeMode()) {
                     UIPreferences.THEME_LIGHT -> false
                     UIPreferences.THEME_DARK -> true
                     else -> androidx.compose.foundation.isSystemInDarkTheme()
                 }
                 ResultSheet(
                     viewModel = viewModel,
                     isExpanded = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded,
                     isDarkTheme = isDarkTheme,
                     openLinksExternally = uiPreferences.isOpenLinksExternally()
                 )
             }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        ) {
            CroppingOverlay(
                screenshot = screenshot,
                onImageCropped = { bitmap ->
                    viewModel.onImageCropped(bitmap, context)
                }
            )
        }
    }
}

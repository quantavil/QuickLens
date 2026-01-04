package com.quantavil.quicklens.ui.components

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.quantavil.quicklens.ui.theme.OverlayGradientColors
import com.quantavil.quicklens.utils.ImageUtils
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Composable
fun CroppingOverlay(
    screenshot: Bitmap?,
    onImageCropped: (Bitmap) -> Unit
) {
    val scope = rememberCoroutineScope()

    val currentPathPoints = remember { mutableStateListOf<Offset>() }
    
    var selectionRect by remember { mutableStateOf<Rect?>(null) }
    val selectionAnim = remember { Animatable(0f) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (screenshot != null) 1f else 0f,
        animationSpec = tween(1000), label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (screenshot != null) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    bitmap = screenshot.asImageBitmap(),
                    contentDescription = "Screenshot",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = OverlayGradientColors.map { it.copy(alpha = 0.15f) }
                            )
                        )
                )
            }
        }



        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPathPoints.clear()
                            currentPathPoints.add(offset)
                            selectionRect = null
                            scope.launch { selectionAnim.snapTo(0f) }
                        },
                        onDrag = { change, _ ->
                            currentPathPoints.add(change.position)
                        },
                        onDragEnd = {
                            if (currentPathPoints.isNotEmpty() && screenshot != null) {
                                var minX = Float.MAX_VALUE
                                var minY = Float.MAX_VALUE
                                var maxX = Float.MIN_VALUE
                                var maxY = Float.MIN_VALUE

                                currentPathPoints.forEach { p ->
                                    minX = min(minX, p.x)
                                    minY = min(minY, p.y)
                                    maxX = max(maxX, p.x)
                                    maxY = max(maxY, p.y)
                                }
                                
                                val rect = Rect(
                                    minX.toInt(),
                                    minY.toInt(),
                                    maxX.toInt(),
                                    maxY.toInt()
                                )
                                
                                selectionRect = rect
                                currentPathPoints.clear() 
                                
                                scope.launch {
                                    selectionAnim.animateTo(
                                        targetValue = 1f,
                                        animationSpec = tween(600)
                                    )
                                    
                                    val cropped = ImageUtils.cropBitmap(screenshot, rect)
                                    onImageCropped(cropped)
                                }
                            }
                        }
                    )
                }
        ) {
            if (currentPathPoints.size > 1) {
                val path = Path().apply {
                    moveTo(currentPathPoints.first().x, currentPathPoints.first().y)
                    for (i in 1 until currentPathPoints.size) {
                        lineTo(currentPathPoints[i].x, currentPathPoints[i].y)
                    }
                }
                
                drawPath(
                    path = path,
                    brush = Brush.linearGradient(OverlayGradientColors),
                    style = Stroke(width = 30f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                    alpha = 0.6f
                )
                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(width = 12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            if (selectionRect != null && selectionAnim.value > 0f) {
                val rect = selectionRect!!
                val progress = selectionAnim.value
                val left = rect.left.toFloat()
                val top = rect.top.toFloat()
                val right = rect.right.toFloat()
                val bottom = rect.bottom.toFloat()
                
                val width = right - left
                val height = bottom - top
                
                val cornerRadius = 32f * progress
                
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(left, top),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = 6f),
                    alpha = progress
                )
            }
        }
    }
}

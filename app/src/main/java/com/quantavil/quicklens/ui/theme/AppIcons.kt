package com.quantavil.quicklens.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AppIcons {
    val History: ImageVector
        get() = if (_history != null) _history!! else {
            _history = materialIcon(name = "History") {
                path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
                    moveTo(13.0f, 3.0f)
                    curveTo(8.03f, 3.0f, 4.0f, 7.03f, 4.0f, 12.0f)
                    horizontalLineTo(1.0f)
                    lineToRelative(3.89f, 3.89f)
                    lineToRelative(0.07f, 0.14f)
                    lineTo(9.0f, 12.0f)
                    horizontalLineTo(6.0f)
                    curveToRelative(0.0f, -3.87f, 3.13f, -7.0f, 7.0f, -7.0f)
                    reflectiveCurveToRelative(7.0f, 3.13f, 7.0f, 7.0f)
                    reflectiveCurveToRelative(-3.13f, 7.0f, -7.0f, 7.0f)
                    curveToRelative(-1.93f, 0.0f, -3.68f, -0.79f, -4.95f, -2.05f)
                    lineToRelative(-1.42f, 1.42f)
                    curveTo(8.24f, 20.37f, 10.51f, 21.0f, 13.0f, 21.0f)
                    curveToRelative(4.97f, 0.0f, 9.0f, -4.03f, 9.0f, -9.0f)
                    reflectiveCurveToRelative(-4.03f, -9.0f, -9.0f, -9.0f)
                    close()
                    moveTo(12.0f, 8.0f)
                    verticalLineToRelative(7.0f)
                    horizontalLineToRelative(4.0f)
                    verticalLineToRelative(-2.0f)
                    horizontalLineToRelative(-3.0f)
                    verticalLineTo(8.0f)
                    horizontalLineToRelative(-1.0f)
                    close()
                }
            }
            _history!!
        }
    private var _history: ImageVector? = null

    val CheckCircle: ImageVector
        get() = if (_checkCircle != null) _checkCircle!! else {
            _checkCircle = materialIcon(name = "CheckCircle") {
                path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
                    moveTo(12.0f, 2.0f)
                    curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                    reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
                    reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
                    reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
                    close()
                    moveTo(10.0f, 17.0f)
                    lineToRelative(-5.0f, -5.0f)
                    lineToRelative(1.41f, -1.41f)
                    lineTo(10.0f, 14.17f)
                    lineToRelative(7.59f, -7.59f)
                    lineTo(19.0f, 8.0f)
                    lineToRelative(-9.0f, 9.0f)
                    close()
                }
            }
            _checkCircle!!
        }
    private var _checkCircle: ImageVector? = null

    val Accessibility: ImageVector
        get() = if (_accessibility != null) _accessibility!! else {
            _accessibility = materialIcon(name = "Accessibility") {
                path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
                    moveTo(12.0f, 2.0f)
                    curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
                    reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
                    reflectiveCurveToRelative(-2.0f, -0.9f, -2.0f, -2.0f)
                    reflectiveCurveToRelative(0.9f, -2.0f, 2.0f, -2.0f)
                    close()
                    moveTo(21.0f, 9.0f)
                    horizontalLineToRelative(-6.0f)
                    verticalLineToRelative(13.0f)
                    horizontalLineToRelative(-2.0f)
                    verticalLineToRelative(-6.0f)
                    horizontalLineToRelative(-2.0f)
                    verticalLineToRelative(6.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(9.0f)
                    horizontalLineTo(3.0f)
                    verticalLineTo(7.0f)
                    horizontalLineToRelative(18.0f)
                    verticalLineToRelative(2.0f)
                    close()
                }
            }
            _accessibility!!
        }
    private var _accessibility: ImageVector? = null

    val Assistant: ImageVector
        get() = if (_assistant != null) _assistant!! else {
            _assistant = materialIcon(name = "Assistant") {
                path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
                    moveTo(19.0f, 2.0f)
                    horizontalLineTo(5.0f)
                    curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                    verticalLineToRelative(14.0f)
                    curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                    horizontalLineToRelative(4.0f)
                    lineToRelative(3.0f, 3.0f)
                    lineToRelative(3.0f, -3.0f)
                    horizontalLineToRelative(4.0f)
                    curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                    verticalLineTo(4.0f)
                    curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                    close()
                    moveTo(13.88f, 12.88f)
                    lineTo(12.0f, 17.0f)
                    lineToRelative(-1.88f, -4.12f)
                    lineTo(6.0f, 11.0f)
                    lineToRelative(4.12f, -1.88f)
                    lineTo(12.0f, 5.0f)
                    lineToRelative(1.88f, 4.12f)
                    lineTo(18.0f, 11.0f)
                    lineToRelative(-4.12f, 1.88f)
                    close()
                }
            }
            _assistant!!
        }
    private var _assistant: ImageVector? = null

    val ChevronRight: ImageVector
        get() = if (_chevronRight != null) _chevronRight!! else {
            _chevronRight = materialIcon(name = "ChevronRight") {
                path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
                    moveTo(10.0f, 6.0f)
                    lineTo(8.59f, 7.41f)
                    lineTo(13.17f, 12.0f)
                    lineToRelative(-4.58f, 4.59f)
                    lineTo(10.0f, 18.0f)
                    lineToRelative(6.0f, -6.0f)
                    close()
                }
            }
            _chevronRight!!
        }
    private var _chevronRight: ImageVector? = null

    val ContentCopy: ImageVector
        get() = if (_contentCopy != null) _contentCopy!! else {
            _contentCopy = materialIcon(name = "ContentCopy") {
                path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
                    moveTo(16.0f, 1.0f)
                    horizontalLineTo(4.0f)
                    curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                    verticalLineToRelative(14.0f)
                    horizontalLineToRelative(2.0f)
                    verticalLineTo(3.0f)
                    horizontalLineToRelative(12.0f)
                    verticalLineTo(1.0f)
                    close()
                    moveTo(19.0f, 5.0f)
                    horizontalLineTo(8.0f)
                    curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                    verticalLineToRelative(14.0f)
                    curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                    horizontalLineToRelative(11.0f)
                    curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                    verticalLineTo(7.0f)
                    curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                    close()
                    moveTo(19.0f, 21.0f)
                    horizontalLineTo(8.0f)
                    verticalLineTo(7.0f)
                    horizontalLineToRelative(11.0f)
                    verticalLineToRelative(14.0f)
                    close()
                }
            }
            _contentCopy!!
        }
    private var _contentCopy: ImageVector? = null

    val ErrorOutline: ImageVector
        get() = if (_errorOutline != null) _errorOutline!! else {
            _errorOutline = materialIcon(name = "ErrorOutline") {
                path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
                    moveTo(11.0f, 15.0f)
                    horizontalLineToRelative(2.0f)
                    verticalLineToRelative(2.0f)
                    horizontalLineToRelative(-2.0f)
                    close()
                    moveTo(11.0f, 7.0f)
                    horizontalLineToRelative(2.0f)
                    verticalLineToRelative(6.0f)
                    horizontalLineToRelative(-2.0f)
                    close()
                    moveTo(11.99f, 2.0f)
                    curveTo(6.47f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                    reflectiveCurveToRelative(4.47f, 10.0f, 9.99f, 10.0f)
                    curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
                    reflectiveCurveTo(17.52f, 2.0f, 11.99f, 2.0f)
                    close()
                    moveTo(12.0f, 20.0f)
                    curveToRelative(-4.42f, 0.0f, -8.0f, -3.58f, -8.0f, -8.0f)
                    reflectiveCurveToRelative(3.58f, -8.0f, 8.0f, -8.0f)
                    reflectiveCurveToRelative(8.0f, 3.58f, 8.0f, 8.0f)
                    reflectiveCurveToRelative(-3.58f, 8.0f, -8.0f, 8.0f)
                    close()
                }
            }
            _errorOutline!!
        }
    private var _errorOutline: ImageVector? = null

    val Image: ImageVector
        get() = if (_image != null) _image!! else {
            _image = materialIcon(name = "Image") {
                path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
                    moveTo(21.0f, 19.0f)
                    verticalLineTo(5.0f)
                    curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                    horizontalLineTo(5.0f)
                    curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                    verticalLineToRelative(14.0f)
                    curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                    horizontalLineToRelative(14.0f)
                    curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                    close()
                    moveTo(8.5f, 13.5f)
                    lineToRelative(2.5f, 3.01f)
                    lineTo(14.5f, 12.0f)
                    lineToRelative(4.5f, 6.0f)
                    horizontalLineTo(5.0f)
                    lineToRelative(3.5f, -4.5f)
                    close()
                }
            }
            _image!!
        }
    private var _image: ImageVector? = null
}

// Helper builder
inline fun materialIcon(
    name: String,
    block: ImageVector.Builder.() -> ImageVector.Builder
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24.0f,
    viewportHeight = 24.0f
).block().build()

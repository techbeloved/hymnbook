package com.techbeloved.hymnbook.shared.ui.icons.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.techbeloved.hymnbook.shared.ui.icons.IconPack

public val IconPack.Youtube: ImageVector
    get() {
        if (_youtube != null) {
            return _youtube!!
        }
        _youtube = Builder(
            name = "Youtube", defaultWidth = 16.0.dp, defaultHeight = 16.0.dp,
            viewportWidth = 461.0f, viewportHeight = 461.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFF61C0D)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(365.26f, 67.39f)
                horizontalLineTo(95.74f)
                curveTo(42.87f, 67.39f, 0.0f, 110.26f, 0.0f, 163.14f)
                verticalLineToRelative(134.73f)
                curveToRelative(0.0f, 52.88f, 42.87f, 95.74f, 95.74f, 95.74f)
                horizontalLineToRelative(269.51f)
                curveToRelative(52.88f, 0.0f, 95.74f, -42.87f, 95.74f, -95.74f)
                verticalLineTo(163.14f)
                curveTo(461.0f, 110.26f, 418.14f, 67.39f, 365.26f, 67.39f)
                close()
                moveTo(300.51f, 237.06f)
                lineToRelative(-126.06f, 60.12f)
                curveToRelative(-3.36f, 1.6f, -7.24f, -0.85f, -7.24f, -4.57f)
                verticalLineTo(168.61f)
                curveToRelative(0.0f, -3.77f, 3.98f, -6.22f, 7.35f, -4.51f)
                lineToRelative(126.06f, 63.88f)
                curveTo(304.36f, 229.87f, 304.3f, 235.25f, 300.51f, 237.06f)
                close()
            }
        }
            .build()
        return _youtube!!
    }

private var _youtube: ImageVector? = null

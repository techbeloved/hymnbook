package com.techbeloved.hymnbook.shared.ui.icons.iconpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import org.jetbrains.compose.ui.tooling.preview.Preview

public val IconPack.XTwitterLogo: ImageVector
    get() {
        if (_icLogo != null) {
            return _icLogo!!
        }
        _icLogo = Builder(name = "IcLogo", defaultWidth = 16.0.dp, defaultHeight = 16.0.dp,
                viewportWidth = 300.0f, viewportHeight = 271.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(236.0f, 0.0f)
                horizontalLineToRelative(46.0f)
                lineToRelative(-101.0f, 115.0f)
                lineToRelative(118.0f, 156.0f)
                horizontalLineToRelative(-92.6f)
                lineToRelative(-72.5f, -94.8f)
                lineToRelative(-83.0f, 94.8f)
                horizontalLineToRelative(-46.0f)
                lineToRelative(107.0f, -123.0f)
                lineToRelative(-113.0f, -148.0f)
                horizontalLineToRelative(94.9f)
                lineToRelative(65.5f, 86.6f)
                close()
                moveTo(219.9f, 244.0f)
                horizontalLineToRelative(25.5f)
                lineToRelative(-165.0f, -218.0f)
                horizontalLineToRelative(-27.4f)
                close()
            }
        }
        .build()
        return _icLogo!!
    }

private var _icLogo: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = IconPack.XTwitterLogo, contentDescription = "")
    }
}

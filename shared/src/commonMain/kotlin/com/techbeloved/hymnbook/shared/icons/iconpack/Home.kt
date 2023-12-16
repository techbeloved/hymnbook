package com.techbeloved.hymnbook.shared.icons.iconpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.techbeloved.hymnbook.shared.icons.IconPack

public val IconPack.Home: ImageVector
    get() {
        if (_icHome != null) {
            return _icHome!!
        }
        _icHome = Builder(name = "IcHome", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 6.35f, viewportHeight = 6.35f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, fillAlpha = 0.978f,
                    strokeAlpha = 0.978f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveToRelative(3.171f, 0.78999f)
                lineToRelative(-2.6415f, 2.7149f)
                verticalLineToRelative(1.8087f)
                curveToRelative(0.0f, 0.13234f, 0.10366f, 0.23888f, 0.23242f, 0.23888f)
                horizontalLineToRelative(1.6104f)
                curveToRelative(0.12876f, 0.0f, 0.23242f, -0.10654f, 0.23242f, -0.23888f)
                verticalLineToRelative(-1.8087f)
                horizontalLineToRelative(1.1412f)
                verticalLineToRelative(1.8087f)
                curveToRelative(0.0f, 0.13234f, 0.10366f, 0.23888f, 0.23242f, 0.23888f)
                horizontalLineToRelative(1.6104f)
                curveToRelative(0.12876f, 0.0f, 0.23242f, -0.10654f, 0.23242f, -0.23888f)
                verticalLineToRelative(-1.8087f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveToRelative(0.418f, -34.73f)
                lineToRelative(15.035f, -15.035f)
                lineToRelative(15.085f, 15.035f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                    fillAlpha = 0.978f, strokeAlpha = 0.978f, strokeLineWidth = 2.0f, strokeLineCap
                    = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType =
                    NonZero) {
                moveToRelative(17.23179f, -11.69401f)
                arcToRelative(3.01383f, 3.01383f, 45.0f, false, false, -3.01383f, 3.01383f)
                arcToRelative(3.01383f, 3.01383f, 0.0f, false, false, 3.01383f, 3.01383f)
                arcToRelative(3.01383f, 3.01383f, 45.0f, false, false, 0.93069f, -0.15141f)
                lineToRelative(1.11515f, 1.93165f)
                lineToRelative(0.45836f, -0.26458f)
                lineToRelative(-1.08261f, -1.87479f)
                arcToRelative(3.01383f, 3.01383f, 135.0f, false, false, 1.59214f, -2.65453f)
                arcToRelative(3.01383f, 3.01383f, 135.0f, false, false, -3.01383f, -3.01383f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, fillAlpha = 0.978f,
                    strokeAlpha = 0.978f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveToRelative(-8.696f, -6.2f)
                curveToRelative(-0.17541f, 0.0f, -0.31656f, 0.14745f, -0.31656f, 0.33068f)
                verticalLineToRelative(0.86932f)
                curveToRelative(0.0f, 0.18323f, 0.14115f, 0.33068f, 0.31656f, 0.33068f)
                horizontalLineToRelative(1.6484f)
                curveToRelative(-0.17541f, 0.0f, -0.31656f, 0.096f, -0.31656f, 0.215f)
                verticalLineToRelative(2.8018f)
                curveToRelative(0.0f, 0.11901f, 0.14115f, 0.215f, 0.31656f, 0.215f)
                horizontalLineToRelative(0.8322f)
                curveToRelative(0.17541f, 0.0f, 0.31687f, -0.096f, 0.31687f, -0.215f)
                verticalLineToRelative(-2.8018f)
                curveToRelative(0.0f, -0.11902f, -0.14146f, -0.215f, -0.31687f, -0.215f)
                horizontalLineToRelative(1.6487f)
                curveToRelative(0.17541f, 0.0f, 0.31656f, -0.14745f, 0.31656f, -0.33068f)
                verticalLineToRelative(-0.86932f)
                curveToRelative(0.0f, -0.18323f, -0.14115f, -0.33068f, -0.31656f, -0.33068f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, fillAlpha = 0.978f,
                    strokeAlpha = 0.978f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveToRelative(14.453f, -0.08f)
                arcToRelative(3.175f, 3.175f, 0.0f, false, false, -3.175f, 3.175f)
                arcToRelative(3.175f, 3.175f, 0.0f, false, false, 3.175f, 3.175f)
                arcToRelative(3.175f, 3.175f, 0.0f, false, false, 1.0839f, -0.19489f)
                lineToRelative(1.1344f, 1.9941f)
                curveToRelative(0.08387f, 0.14698f, 0.23479f, 0.21709f, 0.33841f, 0.15768f)
                lineToRelative(0.20242f, -0.11605f)
                curveToRelative(0.1036f, -0.0594f, 0.11955f, -0.22543f, 0.03588f, -0.37251f)
                lineToRelative(-1.1122f, -1.9556f)
                arcToRelative(3.175f, 3.175f, 0.0f, false, false, 1.4923f, -2.6878f)
                arcToRelative(3.175f, 3.175f, 0.0f, false, false, -3.175f, -3.175f)
                close()
                moveTo(14.453f, 0.37357f)
                arcToRelative(2.7214f, 2.7214f, 0.0f, false, true, 2.7214f, 2.7214f)
                arcToRelative(2.7214f, 2.7214f, 0.0f, false, true, -2.7214f, 2.7214f)
                arcToRelative(2.7214f, 2.7214f, 0.0f, false, true, -2.7214f, -2.7214f)
                arcToRelative(2.7214f, 2.7214f, 0.0f, false, true, 2.7214f, -2.7214f)
                close()
            }
        }
        .build()
        return _icHome!!
    }

private var _icHome: ImageVector? = null

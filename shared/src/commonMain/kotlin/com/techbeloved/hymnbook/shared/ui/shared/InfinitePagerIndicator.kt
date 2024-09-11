package com.techbeloved.hymnbook.shared.ui.shared

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun InfinitePagerIndicator(
    count: Int,
    modifier: Modifier = Modifier,
    activeIndex: Int = 0,
    defaultColor: Color = MaterialTheme.colorScheme.secondary,
    activeColor: Color = MaterialTheme.colorScheme.primary,
) {
    Row(modifier) {
        Canvas(Modifier.size(4.dp)) {
            drawCircle(defaultColor)
        }
    }
}

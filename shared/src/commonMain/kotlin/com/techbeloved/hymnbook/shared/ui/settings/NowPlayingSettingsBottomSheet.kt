@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun NowPlayingSettingsBottomSheet(
    onDismiss: () -> Unit,
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    modifier: Modifier = Modifier,
    bottomSheetState: SheetState = rememberModalBottomSheetState(),
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        modifier = modifier,
    ) {
        ZoomButtons(
            onZoomIn = onZoomIn,
            onZoomOut = onZoomOut,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun ZoomButtons(
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Surface(
            onClick = onZoomOut,
            shape = RoundedCornerShape(
                topStartPercent = 50,
                bottomStartPercent = 50,
                topEndPercent = 0,
                bottomEndPercent = 0,
            ),
            modifier = Modifier.weight(1f)
                .height(48.dp),
        ) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "A", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.width(4.dp))
        Surface(
            onClick = onZoomIn,
            shape = RoundedCornerShape(
                topStartPercent = 0,
                bottomStartPercent = 0,
                topEndPercent = 50,
                bottomEndPercent = 50,
            ),
            modifier = Modifier.weight(1f)
                .height(48.dp),
        ) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "A", style = MaterialTheme.typography.headlineLarge)
            }
        }
    }
}

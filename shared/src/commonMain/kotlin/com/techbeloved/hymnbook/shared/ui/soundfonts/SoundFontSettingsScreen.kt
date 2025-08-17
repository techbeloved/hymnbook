@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.soundfonts

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable

@Serializable
internal object SoundFontSettingsScreen

@Composable
internal fun SoundFontSettingsScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SoundFontSettingsViewModel = viewModel(factory = SoundFontSettingsViewModel.Factory),
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        modifier = modifier,
        scrimColor = Color.Transparent,
    ) {
        Text(
            text = "Choose a sound font",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Text(
            text = "To play midi tunes, you  need a sound font",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
    }
}

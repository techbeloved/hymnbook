package com.techbeloved.hymnbook.shared.ui.soundfonts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.techbeloved.hymnbook.shared.generated.Res
import com.techbeloved.hymnbook.shared.generated.content_description_download_soundfont
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SoundFontDownloadButton(
    onOpenSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    FilledTonalIconButton(
        onClick = onOpenSettingsClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.CloudDownload,
            contentDescription = stringResource(Res.string.content_description_download_soundfont),
        )
    }
}

package com.techbeloved.hymnbook.shared.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.techbeloved.hymnbook.shared.ui.detail.BottomControlsUi
import com.techbeloved.media.AudioItem

@Preview
@Composable
private fun BottomControlsUiPreview() {

    MaterialTheme {
        BottomControlsUi(
            title = "Hymn 10",
            audioItem = AudioItem(
                uri = "files/sample2.mp3",
                title = "Hymn of the ages",
                album = "Hymnbook",
                artist = "Gospel",
            ),
            onPreviousButtonClick = {},
            onNextButtonClick = {},
        )
    }
}

package com.techbeloved.hymnbook.shared.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.techbeloved.hymnbook.shared.ui.detail.BottomControlsUi
import com.techbeloved.hymnbook.shared.ui.search.AppSearchBar
import com.techbeloved.hymnbook.shared.ui.search.SearchState
import com.techbeloved.hymnbook.shared.ui.search.SearchUi
import com.techbeloved.media.AudioItem

@Preview
@Composable
private fun BottomControlsUiPreview() {
    MaterialTheme {
        BottomControlsUi(
            audioItem = AudioItem(
                absolutePath = "files/sample2.mp3",
                relativePath = "relative/path",
                title = "Hymn of the ages",
                artist = "Gospel",
                album = "Hymnbook",
                mediaId = "sample2",
            ),
            onPreviousButtonClick = {},
            onNextButtonClick = {},
            onShowSettingsBottomSheet = {},
        )
    }
}

@Preview
@Composable
private fun SearchBarPreview() {
    MaterialTheme {
        AppSearchBar(
            onSearch = {},
            placeholderText = "Search hymns",
            onQueryChange = {},
            query = "",
        )
    }
}

@Preview
@Composable
private fun SearchUiPreview() {
    MaterialTheme {
        SearchUi(
            state = SearchState.Default,
            query = "",
            onSearch = {},
            onQueryChange = {},
        )
    }
}

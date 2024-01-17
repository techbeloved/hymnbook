package com.techbeloved.hymnbook.shared.ui.listing

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.techbeloved.hymnbook.shared.model.SongTitle
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun HymnListingUi(
    hymnItems: ImmutableList<SongTitle>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(hymnItems, SongTitle::id) { item ->
            ListItem(
                headlineContent = {
                    Text(text = item.title)
                },
                leadingContent = {
                    Text(
                        text = item.songbookEntry.orEmpty(),
                        modifier = Modifier.width(32.dp),
                        textAlign = TextAlign.End,
                    )
                },
                supportingContent = if (item.alternateTitle.isNullOrBlank()) {
                    null
                } else {
                    {
                        Text(text = item.alternateTitle)
                    }
                }
            )
        }
    }
}


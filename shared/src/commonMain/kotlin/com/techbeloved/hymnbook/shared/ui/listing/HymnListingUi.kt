package com.techbeloved.hymnbook.shared.ui.listing

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.techbeloved.hymnbook.shared.model.HymnItem
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun HymnListingUi(
    hymnItems: ImmutableList<HymnItem>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(hymnItems, HymnItem::id) { item ->
            ListItem(
                headlineContent = {
                    Text(text = item.title)
                },
                leadingContent = { Text(text = "${item.id}") },
                supportingContent = {
                    Text(text = item.subtitle)
                }
            )
        }
    }
}


@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.songbook

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techbeloved.hymnbook.SongbookEntity
import com.techbeloved.hymnbook.shared.ui.theme.AppTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun SongbookSelector(
    songbooks: ImmutableList<SongbookEntity>,
    selectedSongbook: SongbookEntity?,
    onSongbookSelected: (SongbookEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                )
                .clickable(onClickLabel = "Select Songbook") { }
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                text = selectedSongbook?.name.orEmpty(),
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )
            Spacer(Modifier.width(width = 8.dp))
            TrailingIcon(expanded = isExpanded)
        }
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            songbooks.forEach { songbook ->
                DropdownMenuItem(
                    text = { Text(songbook.name) },
                    onClick = {
                        onSongbookSelected(songbook)
                        isExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }

        }
    }
}

@Composable
private fun TrailingIcon(
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = Icons.Filled.KeyboardArrowDown,
        contentDescription = null,
        modifier = modifier.rotate(degrees = if (expanded) 180f else 0f),
    )
}

@Composable
@Preview
private fun PreviewSongbookSelector() {
    val songbook = SongbookEntity(
        publisher = "publisher1",
        name = "Songbook 1"
    )
    AppTheme {
        SongbookSelector(
            songbooks = persistentListOf(
                songbook,
            ),
            onSongbookSelected = {},
            selectedSongbook = songbook,
        )
    }
}

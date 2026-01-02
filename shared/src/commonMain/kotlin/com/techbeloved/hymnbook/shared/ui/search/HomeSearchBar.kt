@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dialpad
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techbeloved.hymnbook.shared.ui.theme.AppTheme

@Composable
internal fun HomeSearchBar(
    onSearchClick: () -> Unit,
    onSpeedDialClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .height(48.dp),
        onClick = {
            onSearchClick()
        },
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = CircleShape,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
            )

            Text(
                text = placeholder.orEmpty(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )

            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above
                    ),
                tooltip = { PlainTooltip { Text("Speed dial") } },
                state = rememberTooltipState(),
            ) {
                IconButton(
                    onClick = onSpeedDialClick
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Dialpad,
                        contentDescription = "Speed dial",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHomeSearchBar() {
    AppTheme {
        HomeSearchBar(
            onSearchClick = {},
            onSpeedDialClick = {},
            placeholder = "Search"
        )
    }
}

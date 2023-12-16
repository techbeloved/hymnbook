package com.techbeloved.hymnbook.shared.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSearchBar(onSearch: (String) -> Unit) {
    val (searchQuery, updateSearchQuery) = rememberSaveable { mutableStateOf("") }
    val (isActive, updateIsActive) = rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .semantics { isTraversalGroup = true }
            .zIndex(1f)
            .fillMaxWidth(),
    ) {
        SearchBar(
            query = searchQuery,
            active = isActive,
            onActiveChange = updateIsActive,
            onQueryChange = updateSearchQuery,
            onSearch = onSearch,
            placeholder = { Text(text = "Search Hymns") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = { Icon(Icons.Rounded.MoreVert, contentDescription = "More options") },
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(4) { idx ->
                    val resultText = "Suggestion $idx"
                    ListItem(
                        modifier = Modifier.clickable {
                            updateSearchQuery(resultText)
                            updateIsActive(false)
                        },
                        headlineContent = {
                            Text(
                                text = resultText,
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        supportingContent = {
                            Text(
                                text = "Additional info",
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        leadingContent = { Icon(Icons.Rounded.Star, contentDescription = null) },
                    )
                }
            }
        }
    }
}



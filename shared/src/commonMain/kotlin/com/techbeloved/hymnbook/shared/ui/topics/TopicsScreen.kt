@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.topics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techbeloved.hymnbook.TopicEntity
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.theme.AppTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Serializable
internal object TopicsScreen

@Composable
internal fun TopicsScreen(
    onTopicSelected: (topic: TopicEntity) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TopicsViewModel = viewModel(factory = TopicsViewModel.Factory),
) {

    val topics by viewModel.state.collectAsState()
    TopicsUi(
        topics = topics,
        onTopicSelected = onTopicSelected,
        modifier = modifier,
    )
}

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun TopicsUi(
    topics: ImmutableList<TopicEntity>,
    onTopicSelected: (topic: TopicEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hazeState = remember { HazeState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            AppTopBar(
                scrollBehaviour = scrollBehavior,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .5f),
                modifier = Modifier.hazeEffect(hazeState, style = HazeMaterials.ultraThin()),
                showUpButton = false,
                title = "Topics",
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0f),
            ) { }
        },
        modifier = modifier,
    ) { innerPadding ->

        FlowRow(
            modifier = Modifier.padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(state = rememberScrollState())
                .hazeSource(hazeState),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.fillMaxWidth().height(8.dp))
            topics.forEachIndexed { index, topic ->
                TopicItem(topic = topic, onClick = { onTopicSelected(topic) })
            }
            Spacer(Modifier.fillMaxWidth().height(16.dp))
        }
    }
}

@Composable
private fun TopicItem(
    topic: TopicEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SuggestionChip(
        modifier = modifier,
        onClick = onClick,
        label = {
            Text(
                text = topic.name,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        border = null,
        shape = RoundedCornerShape(100.dp),
    )
}

@Preview
@Composable
private fun PreviewTopicsUi(modifier: Modifier = Modifier) {
    AppTheme {
        TopicsUi(
            topics = persistentListOf(
                TopicEntity(name = "Worship & Thanksgiving"),
                TopicEntity(name = "Goodness"),
            ),
            onTopicSelected = {},
            modifier = modifier,
        )
    }
}

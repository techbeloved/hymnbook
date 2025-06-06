package com.techbeloved.hymnbook.shared.ui.discover

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.techbeloved.hymnbook.TopicEntity
import com.techbeloved.hymnbook.shared.ui.topics.TopicsScreen

@Composable
internal fun DiscoverTabScreen(
    onTopicSelected: (topic: TopicEntity) -> Unit,
) {
    // For now we use topics screen. Until when the discover feature is ready
    TopicsScreen(
        onTopicSelected = onTopicSelected,
        modifier = Modifier.fillMaxSize(),
    )
}

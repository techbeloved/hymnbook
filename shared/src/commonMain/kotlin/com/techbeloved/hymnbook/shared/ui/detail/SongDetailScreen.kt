package com.techbeloved.hymnbook.shared.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.theme.crimsonText

internal class SongDetailScreen(private val songId: Long) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { SongDetailScreenModel(songId) }
        val state by screenModel.state.collectAsState()
        SongDetailUi(state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SongDetailUi(state: SongUiDetail) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppTopBar(state.title.text, scrollBehaviour = scrollBehavior)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .safeContentPadding(),
        ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                Text(
                    state.content,
                    modifier = Modifier.fillMaxWidth(),
                    fontFamily = crimsonText,
                    fontSize = 18.sp,
                )
            }
        }
    }
}

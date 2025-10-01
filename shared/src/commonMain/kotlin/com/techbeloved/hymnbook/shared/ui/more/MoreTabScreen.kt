@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.techbeloved.hymnbook.shared.AppHost
import com.techbeloved.hymnbook.shared.songshare.ShareAppData
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.share.NativeShareButton
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun MoreTabScreen(
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            AppTopBar(
                showUpButton = false,
                scrollBehaviour = scrollBehavior,
                title = "More",
            )
        },
        bottomBar = {
            // A workaround to apply correct bottom padding to the HomeUi.
            // The Actual Navigation Bar is provided at the top level scaffold
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0f),
            ) { }
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = modifier,
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            ) {
                NativeShareButton {
                    ListItem(
                        headlineContent = { Text(text = "Shared the App") },
                        modifier = Modifier.clickable {
                            // Open app sharing
                            onClick(
                                shareData = ShareAppData(
                                    title = "Download Watchman Hymnbook App",
                                    text = "Download Watchman Hymnbook App",
                                    url = URLBuilder(
                                        protocol = URLProtocol.HTTPS,
                                        host = AppHost
                                    ).buildString(),
                                )
                            )
                        },
                        leadingContent = {
                            Icon(imageVector = Icons.TwoTone.Share, contentDescription = null)
                        }
                    )
                }

                ListItem(
                    headlineContent = { Text(text = "About") },
                    modifier = Modifier.clickable {
                        onAboutClick()
                    },
                    leadingContent = {
                        Icon(imageVector = Icons.TwoTone.Info, contentDescription = null)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun MoreTabScreenPreview() {
    MaterialTheme {
        MoreTabScreen(onAboutClick = { })
    }
}

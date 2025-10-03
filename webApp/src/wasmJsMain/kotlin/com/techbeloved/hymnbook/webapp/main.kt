package com.techbeloved.hymnbook.webapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import hymnbook.webapp.generated.resources.Res
import hymnbook.webapp.generated.resources.app_icon_light
import hymnbook.webapp.generated.resources.app_logo_description
import hymnbook.webapp.generated.resources.download_app_description
import hymnbook.webapp.generated.resources.download_app_title
import hymnbook.webapp.generated.resources.download_on_app_store
import hymnbook.webapp.generated.resources.download_on_appstore_dark
import hymnbook.webapp.generated.resources.download_on_play_store
import hymnbook.webapp.generated.resources.download_on_playstore_color
import hymnbook.webapp.generated.resources.screenshot_android_1
import hymnbook.webapp.generated.resources.screenshot_android_2
import hymnbook.webapp.generated.resources.screenshot_ios_1
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private const val PlayStoreUrl =
    "https://play.google.com/store/apps/details?id=com.techbeloved.hymnbook"

private const val AppStoreUrl = "https://apps.apple.com/us/app/watchman-music/id6748705382"

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        WebAppTheme {
            WebAppPromotion(
                onAppAppStoreClick = {
                    openUrlInBrowser(AppStoreUrl)
                },
                onGooglePlayStoreClick = {
                    openUrlInBrowser(PlayStoreUrl)
                }
            )
        }
    }
}

@Composable
private fun WebAppPromotion(
    onAppAppStoreClick: () -> Unit,
    onGooglePlayStoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                )
            ),
        contentAlignment = Alignment.TopCenter,
    ) {

        Column(modifier = Modifier.widthIn(max = 900.dp)) {
            Spacer(Modifier.height(24.dp))
            Box {
                Row(
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.screenshot_android_1),
                        contentDescription = null,
                        modifier = Modifier.offset(y = 100.dp)
                            .sizeIn(maxWidth = 200.dp),
                    )
                    Column {
                        Spacer(Modifier.height(175.dp))
                        Image(
                            painter = painterResource(Res.drawable.screenshot_ios_1),
                            contentDescription = null,
                            modifier = Modifier
                                .sizeIn(maxWidth = 200.dp),
                        )
                    }
                    Image(
                        painter = painterResource(Res.drawable.screenshot_android_2),
                        contentDescription = null,
                        modifier = Modifier.offset(y = 100.dp)
                            .sizeIn(maxWidth = 200.dp),
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .94f),
                    modifier = Modifier,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.app_icon_light),
                            contentDescription = stringResource(Res.string.app_logo_description),
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = stringResource(Res.string.download_app_title),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(Res.string.download_app_description),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp)
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(
                    onClick = onGooglePlayStoreClick,
                    modifier = Modifier,
                ) {
                    Image(
                        painter = painterResource(Res.drawable.download_on_playstore_color),
                        contentDescription = stringResource(Res.string.download_on_play_store),
                        modifier = Modifier.height(48.dp),
                        contentScale = ContentScale.FillHeight,
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextButton(
                        onClick = onAppAppStoreClick,
                        modifier = Modifier,
                    ) {
                        Box(modifier = Modifier) {
                            Image(
                                painter = painterResource(Res.drawable.download_on_appstore_dark),
                                contentDescription = stringResource(Res.string.download_on_app_store),
                                modifier = Modifier.height(48.dp),
                                contentScale = ContentScale.FillHeight,
                            )
                        }
                    }

                }
            }

        }

    }

}

private fun openUrlInBrowser(url: String, target: String = "_blank") {
    window.open(url = url, target = target)
}

@Preview
@Composable
private fun PreviewWebPromotion() {
    WebAppPromotion(
        onAppAppStoreClick = {},
        onGooglePlayStoreClick = {},
    )
}

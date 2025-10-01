@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)

package com.techbeloved.hymnbook.shared.ui.more.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.twotone.Facebook
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.techbeloved.hymnbook.shared.appversion.defaultAppVersionProvider
import com.techbeloved.hymnbook.shared.generated.Res
import com.techbeloved.hymnbook.shared.generated.about
import com.techbeloved.hymnbook.shared.generated.about_app_description
import com.techbeloved.hymnbook.shared.generated.about_app_title
import com.techbeloved.hymnbook.shared.generated.about_wccrm_description
import com.techbeloved.hymnbook.shared.generated.about_wccrm_title
import com.techbeloved.hymnbook.shared.generated.connect_with_us
import com.techbeloved.hymnbook.shared.generated.instagram_glyph_black
import com.techbeloved.hymnbook.shared.generated.legal
import com.techbeloved.hymnbook.shared.generated.open_source_licenses
import com.techbeloved.hymnbook.shared.generated.privacy_policy
import com.techbeloved.hymnbook.shared.generated.terms_and_conditions
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.icons.IconPack
import com.techbeloved.hymnbook.shared.ui.icons.iconpack.XTwitterLogo
import com.techbeloved.hymnbook.shared.ui.icons.iconpack.Youtube
import com.techbeloved.hymnbook.shared.ui.share.OpenExternalUrlButton
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Serializable
internal object AboutScreen

@Composable
internal fun AboutScreen(
    onOpenSourceLicencesClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsAndConditionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val hazeState = remember { HazeState() }
    Scaffold(
        topBar = {
            AppTopBar(
                showUpButton = true,
                scrollBehaviour = scrollBehavior,
                title = stringResource(Res.string.about),
                modifier = Modifier.hazeEffect(hazeState, style = HazeMaterials.ultraThin()),
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        modifier = modifier,
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .hazeSource(hazeState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                AboutAppSection()
                Spacer(modifier = Modifier.height(16.dp))
                AboutWccrmSection()
                Spacer(modifier = Modifier.height(16.dp))
                SocialMediaSection()
                Spacer(modifier = Modifier.height(16.dp))
                LegalSection(
                    onOpenSourceLicencesClick = onOpenSourceLicencesClick,
                    onPrivacyPolicyClick = onPrivacyPolicyClick,
                    onTermsAndConditionsClick = onTermsAndConditionsClick,
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Version: ${defaultAppVersionProvider.get().name}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp)
                )

            }
        }
    }
}

@Composable
private fun AboutAppSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = stringResource(Res.string.about_app_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.about_app_description),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun AboutWccrmSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = stringResource(Res.string.about_wccrm_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.about_wccrm_description),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun SocialMediaSection(modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(
            text = stringResource(Res.string.connect_with_us),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp, end = 16.dp)
        )
        OpenExternalUrlButton {
            SocialMediaItem(title = "YouTube", imageVector = IconPack.Youtube) {
                onClick("https://www.youtube.com/@vowtelevision")
            }
        }
        OpenExternalUrlButton {
            SocialMediaItem(title = "Facebook", imageVector = Icons.TwoTone.Facebook) {
                onClick("https://www.facebook.com/vowtv")
            }
        }
        OpenExternalUrlButton {
            SocialMediaItem(
                title = "Instagram",
                drawableResource = Res.drawable.instagram_glyph_black
            ) {
                onClick("https://www.instagram.com/vowtv")
            }
        }
        OpenExternalUrlButton {
            SocialMediaItem(title = "X (Twitter)", imageVector = IconPack.XTwitterLogo) {
                onClick("https://x.com/vowtelevision")
            }
        }
    }
}

@Composable
private fun SocialMediaItem(title: String, imageVector: ImageVector, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(text = title) },
        leadingContent = {
            Icon(
                imageVector = imageVector,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
    )
}

@Composable
private fun SocialMediaItem(
    title: String,
    drawableResource: DrawableResource,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text = title) },
        leadingContent = {
            Icon(
                painter = painterResource(drawableResource),
                contentDescription = title,
                modifier = Modifier.size(24.dp),
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
    )
}

@Composable
private fun LegalSection(
    onOpenSourceLicencesClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsAndConditionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = stringResource(resource = Res.string.legal),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 16.dp),
        )
        ListItem(
            headlineContent = { Text(stringResource(Res.string.open_source_licenses)) },
            modifier = Modifier.clickable { onOpenSourceLicencesClick() }
                .padding(horizontal = 16.dp))
        ListItem(
            headlineContent = { Text(stringResource(Res.string.privacy_policy)) },
            modifier = Modifier.clickable { onPrivacyPolicyClick() }
                .padding(horizontal = 16.dp))
        ListItem(
            headlineContent = { Text(stringResource(Res.string.terms_and_conditions)) },
            modifier = Modifier.clickable { onTermsAndConditionsClick() }
                .padding(horizontal = 16.dp))
    }
}

@Preview
@Composable
private fun AboutScreenPreview() {
    AboutScreen(
        onOpenSourceLicencesClick = {},
        onPrivacyPolicyClick = {},
        onTermsAndConditionsClick = {},
    )
}

package com.techbeloved.hymnbook.shared.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppMaybe
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.techbeloved.hymnbook.shared.generated.Res
import com.techbeloved.hymnbook.shared.generated.content_unavailable
import com.techbeloved.hymnbook.shared.generated.content_unavailable_button_text
import com.techbeloved.hymnbook.shared.generated.content_unavailable_explanation
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CopyrightSongNotAvailableUi(
    onOnlineSearchButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = CircleShape,
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = Icons.Default.GppMaybe,
                    contentDescription = null,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.content_unavailable),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.content_unavailable_explanation),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick =  onOnlineSearchButtonClick) {
            Text(text = stringResource(Res.string.content_unavailable_button_text))
        }
    }
}

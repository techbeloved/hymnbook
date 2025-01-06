package com.techbeloved.media

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class DemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column {
                PlayerControlViewPreview()
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PlayerControlViewPreview() {
    MaterialTheme {
        MediaPlayerControls(modifier = Modifier.height(300.dp))
    }
}

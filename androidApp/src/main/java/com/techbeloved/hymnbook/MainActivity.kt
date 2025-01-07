package com.techbeloved.hymnbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.techbeloved.hymnbook.shared.App
import com.techbeloved.media.DefaultMediaControllerDisposer
import com.techbeloved.media.MediaControllerDisposer

class MainActivity : ComponentActivity(),
    MediaControllerDisposer by DefaultMediaControllerDisposer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }

    override fun onStop() {
        onDispose()
        super.onStop()
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    App()
}

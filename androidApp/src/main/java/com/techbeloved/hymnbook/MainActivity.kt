package com.techbeloved.hymnbook

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.techbeloved.hymnbook.shared.App
import com.techbeloved.media.DefaultMediaControllerDisposer
import com.techbeloved.media.MediaControllerDisposer

class MainActivity : AppCompatActivity(),
    MediaControllerDisposer by DefaultMediaControllerDisposer() {

    private val analytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        analytics
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

package com.techbeloved.hymnbook.inappupdates

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleStartEffect
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.ktx.installStatus
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun FlexibleUpdates(
    appUpdateManager: AppUpdateManager,
    appUpdateInfo: AppUpdateInfo,
) {

    val scope = rememberCoroutineScope()

    var showUpdateDownloaded by remember { mutableStateOf(false) }

    val activityLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {

        }

    LaunchedEffect(Unit) {
        runCatching {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                activityLauncher,
                AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
            )
        }
    }

    LifecycleStartEffect(Unit) {
        val listener = InstallStateUpdatedListener { installState ->
            if (installState.installStatus == InstallStatus.DOWNLOADED) {
                showUpdateDownloaded = true
            }
        }
        appUpdateManager.registerListener(listener)
        onStopOrDispose {
            appUpdateManager.unregisterListener(listener)
        }
    }

    if (showUpdateDownloaded) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        runCatching { appUpdateManager.completeUpdate().await() }
                    }
                }) {
                    Text("Restart")
                }
            }, text = {
                Text("An update has just been downloaded.")
            }
        )
    }
}

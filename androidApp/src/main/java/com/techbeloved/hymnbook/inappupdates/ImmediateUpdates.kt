package com.techbeloved.hymnbook.inappupdates

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType

@Composable
fun ImmediateUpdates(
    appUpdateManager: AppUpdateManager,
    appUpdateInfo: AppUpdateInfo,
) {
    val activityLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {

        }
    LaunchedEffect(Unit) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            activityLauncher,
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
        )
    }
}

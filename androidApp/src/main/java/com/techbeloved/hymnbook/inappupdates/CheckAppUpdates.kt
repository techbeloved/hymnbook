package com.techbeloved.hymnbook.inappupdates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.clientVersionStalenessDays
import com.google.android.play.core.ktx.requestAppUpdateInfo
import com.google.android.play.core.ktx.updatePriority

private const val DaysAllowedForFlexibleUpdate = 60
private const val HighUpdatePriority = 4

@Composable
fun CheckAppUpdates(
    appUpdateManager: AppUpdateManager,
) {

    var appUpdateInfo by remember { mutableStateOf<AppUpdateInfo?>(null) }

    with(appUpdateInfo) {
        when (this?.let{ checkUpdateStatus(it)}) {
            AppUpdateStatus.Flexible -> FlexibleUpdates(appUpdateManager, this)
            AppUpdateStatus.Immediate -> ImmediateUpdates(appUpdateManager, this)
            AppUpdateStatus.Stalled -> ImmediateUpdates(appUpdateManager, this)
            AppUpdateStatus.None, null -> {
                // Do nothing
            }
        }
    }


    LaunchedEffect(Unit) {
        val updateInfo = runCatching { appUpdateManager.requestAppUpdateInfo() }.getOrNull()
        appUpdateInfo = updateInfo
    }

}

private fun checkUpdateStatus(appUpdateInfo: AppUpdateInfo): AppUpdateStatus {
    return when (appUpdateInfo.updateAvailability()) {
        UpdateAvailability.UPDATE_AVAILABLE -> {
            if ((appUpdateInfo.clientVersionStalenessDays ?: -1) < DaysAllowedForFlexibleUpdate
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                && appUpdateInfo.updatePriority < HighUpdatePriority
            ) AppUpdateStatus.Flexible
            else AppUpdateStatus.Immediate
        }

        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> AppUpdateStatus.Stalled
        else -> AppUpdateStatus.None
    }

}

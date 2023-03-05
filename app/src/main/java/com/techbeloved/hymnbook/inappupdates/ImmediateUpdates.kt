package com.techbeloved.hymnbook.inappupdates

import androidx.activity.ComponentActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import javax.inject.Inject

class ImmediateUpdates @Inject constructor(private val appUpdateManager: AppUpdateManager) {
    operator fun invoke(appUpdateInfo: AppUpdateInfo, activity: ComponentActivity) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE,
            activity,
            CheckAppUpdates.REQUEST_CODE
        )
    }
}

package com.techbeloved.hymnbook.inappupdates

import android.view.View
import androidx.activity.ComponentActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.clientVersionStalenessDays
import javax.inject.Inject

class CheckAppUpdates @Inject constructor(
    private val appUpdateManager: AppUpdateManager,
    private val flexibleUpdates: FlexibleUpdates,
    private val immediateUpdates: ImmediateUpdates,
) {

    operator fun invoke(activity: ComponentActivity, rootView: View) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            when (checkUpdateStatus(appUpdateInfo)) {
                AppUpdateStatus.Flexible -> flexibleUpdates(activity, rootView)
                AppUpdateStatus.Immediate -> immediateUpdates(appUpdateInfo, activity)
                AppUpdateStatus.Stalled -> immediateUpdates(appUpdateInfo, activity)
                AppUpdateStatus.None -> {
                    // Do nothing
                }
            }
        }
    }

    private fun checkUpdateStatus(appUpdateInfo: AppUpdateInfo): AppUpdateStatus {
        return when (appUpdateInfo.updateAvailability()) {
            UpdateAvailability.UPDATE_AVAILABLE -> {
                if ((appUpdateInfo.clientVersionStalenessDays ?: -1) < DAYS_FOR_FLEXIBLE_UPDATE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) AppUpdateStatus.Flexible
                else AppUpdateStatus.Immediate
            }
            UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> AppUpdateStatus.Stalled
            else -> AppUpdateStatus.None
        }

    }

    companion object {
        private const val DAYS_FOR_FLEXIBLE_UPDATE = 7
        const val REQUEST_CODE = 12
    }
}

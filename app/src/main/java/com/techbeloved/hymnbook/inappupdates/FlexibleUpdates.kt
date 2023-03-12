package com.techbeloved.hymnbook.inappupdates

import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.ktx.installStatus
import com.techbeloved.hymnbook.R
import javax.inject.Inject

class FlexibleUpdates @Inject constructor(
    private val appUpdateManager: AppUpdateManager
) {

    operator fun invoke(
        appUpdateInfo: AppUpdateInfo,
        activity: ComponentActivity,
        rootView: View
    ) {
        val listener = InstallStateUpdatedListener { installState ->
            if (installState.installStatus == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate(rootView)
            }
        }

        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                appUpdateManager.registerListener(listener)
            }

            override fun onStop(owner: LifecycleOwner) {
                appUpdateManager.unregisterListener(listener)
            }
        })

        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.FLEXIBLE,
            activity,
            CheckAppUpdates.REQUEST_CODE
        )
    }

    private fun popupSnackbarForCompleteUpdate(rootView: View) {
        Snackbar.make(rootView, "An update has just been downloaded.", Snackbar.LENGTH_INDEFINITE)
            .apply {
                setAction("RESTART") { appUpdateManager.completeUpdate() }
                setActionTextColor(ContextCompat.getColor(rootView.context, R.color.colorAccent))
                show()
            }
    }

}

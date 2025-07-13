package com.techbeloved.hymnbook.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import com.techbeloved.hymnbook.shared.analytics.AppAnalytics
import com.techbeloved.hymnbook.shared.analytics.TrackingBundle
import com.techbeloved.hymnbook.shared.analytics.TrackingEvent
import com.techbeloved.hymnbook.shared.analytics.TrackingParam

class FirebaseAppAnalytics : AppAnalytics {
    override fun track(bundle: TrackingBundle) {
        Firebase.analytics.logEvent(bundle.event.toFirebaseEvent()) {
            bundle.params.forEach { (param, value) ->
                param(param.toFirebaseParam(), value)
            }
        }
    }

    override fun setDefaultParams(params: Map<String, String>) {
        Firebase.analytics.setDefaultEventParameters(
            Bundle().apply {
                params.forEach { (key, value) ->
                    putString(key, value)
                }
            }
        )
    }

    private fun TrackingEvent.toFirebaseEvent(): String = when (this) {
        TrackingEvent.ScreenView -> FirebaseAnalytics.Event.SCREEN_VIEW
        TrackingEvent.ActionSearch -> FirebaseAnalytics.Event.SEARCH
        TrackingEvent.ActionSelectItem -> FirebaseAnalytics.Event.SELECT_ITEM
        TrackingEvent.ActionShare -> FirebaseAnalytics.Event.SHARE
        TrackingEvent.ViewContent -> FirebaseAnalytics.Event.SELECT_CONTENT
    }

    private fun TrackingParam.toFirebaseParam(): String = when (this) {
        TrackingParam.ItemName -> FirebaseAnalytics.Param.ITEM_NAME
        TrackingParam.ItemId -> FirebaseAnalytics.Param.ITEM_ID
        TrackingParam.ItemCategory -> FirebaseAnalytics.Param.ITEM_CATEGORY
        TrackingParam.ScreenName -> FirebaseAnalytics.Param.SCREEN_NAME
        TrackingParam.ScreenClass -> FirebaseAnalytics.Param.SCREEN_CLASS
        TrackingParam.SearchTerm -> FirebaseAnalytics.Param.SEARCH_TERM
        TrackingParam.Content -> FirebaseAnalytics.Param.CONTENT
        TrackingParam.ContentType -> FirebaseAnalytics.Param.CONTENT_TYPE
        TrackingParam.Method -> FirebaseAnalytics.Param.METHOD
    }
}

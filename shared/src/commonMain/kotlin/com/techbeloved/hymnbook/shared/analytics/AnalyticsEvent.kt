package com.techbeloved.hymnbook.shared.analytics

internal data class AnalyticsEvent(
    val tracking: TrackingEvent,
    val name: TrackingName,
    val params: Map<TrackingParam, String>
)

internal fun screenView(
    name: TrackingName,
    screenClass: String,
    params: Map<TrackingParam, String> = emptyMap(),
) =
    AnalyticsEvent(
        tracking = TrackingEvent.ScreenView,
        name = name,
        params = buildMap {
            put(TrackingParam.ScreenName, name.name)
            put(TrackingParam.ScreenClass, screenClass)
            putAll(params)
        },
    )

internal fun actionSearch(
    context: TrackingName,
    query: String,
    params: Map<TrackingParam, String> = emptyMap(),
) = AnalyticsEvent(
    tracking = TrackingEvent.ActionSearch,
    name = context,
    params = buildMap { put(TrackingParam.SearchTerm, query); putAll(params) },
)

internal fun actionSelectItem(
    itemCategory: String,
    itemId: String,
    itemName: String,
    params: Map<TrackingParam, String> = emptyMap(),
) = AnalyticsEvent(
    tracking = TrackingEvent.ActionSelectItem,
    name = TrackingName(itemName),
    params = buildMap {
        put(TrackingParam.ItemName, itemName)
        put(TrackingParam.ItemId, itemId)
        put(TrackingParam.ItemCategory, itemCategory)
        putAll(params)
    },
)

internal fun actionUpdateSettings(
    settingsId: String,
    value: String,
    params: Map<TrackingParam, String> = emptyMap(),
) = AnalyticsEvent(
    tracking = TrackingEvent.ActionSelectItem,
    name = TrackingName(value),
    params = buildMap {
        put(TrackingParam.ItemName, value)
        put(TrackingParam.ItemId, settingsId)
        put(TrackingParam.ItemCategory, "action_update_settings")
        putAll(params)
    },
)

internal fun actionShare(
    name: TrackingName,
    method: String,
    contentType: String,
    itemId: String,
    params: Map<TrackingParam, String> = emptyMap(),
) = AnalyticsEvent(
    tracking = TrackingEvent.ActionShare,
    name = name,
    params = buildMap {
        put(TrackingParam.Method, method)
        put(TrackingParam.ContentType, contentType)
        put(TrackingParam.ItemId, itemId)
        put(TrackingParam.ItemName, name.name)
        putAll(params)
    },
)

internal fun actionViewContent(
    name: String,
    contentType: String,
    itemId: String,
    params: Map<TrackingParam, String> = emptyMap(),
) = AnalyticsEvent(
    tracking = TrackingEvent.ViewContent,
    name = TrackingName(name),
    params = buildMap {
        put(TrackingParam.Content, name)
        put(TrackingParam.ContentType, contentType)
        put(TrackingParam.ItemId, itemId)
        putAll(params)
    },
)

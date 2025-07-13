package com.techbeloved.hymnbook.shared.model.assetimport

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BundledAssetManifest(
    val openlyrics: List<BundledAsset>,
    val json: List<BundledAsset>,
)

@Serializable
internal data class BundledAsset(
    val path: String,
    val type: AssetType,
) {
    val fullPath get() = "files/$path"
}

internal enum class AssetType {
    @SerialName("zip")
    ZIP,
    @SerialName("json")
    JSON,
}

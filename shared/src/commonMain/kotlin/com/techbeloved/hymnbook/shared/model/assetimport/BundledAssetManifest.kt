package com.techbeloved.hymnbook.shared.model.assetimport

import com.techbeloved.hymnbook.shared.generated.Res
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
    val fullPath get() = Res.getUri( "files/$path")
}

internal enum class AssetType {
    @SerialName("zip")
    ZIP,
    @SerialName("json")
    JSON,
}

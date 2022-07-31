package com.techbeloved.hymnbook.utils

const val DYNAMIC_LINK_DOMAIN = "https://hymnbook.page.link"
const val MINIMUM_VERSION_FOR_SHARE_LINK = 10
const val WCCRM_LOGO_URL = "https://firebasestorage.googleapis.com/v0/b/hymnbook-50b7e.appspot.com/o/wccrm_logo.jpg?alt=media&token=8f338e20-b8e4-4ca7-8e9b-997f882ab18e"

// content://hymnbook.com/{category}/{categoryId}/{hymns}/{hymnId}
const val AUTHORITY = "hymnbook.com"
const val SCHEME_NORMAL = "https"
const val SCHEME_HTTP = "https"

// Supported hymn listing categories
const val CATEGORY_WCCRM = "wccrm"
const val CATEGORY_TOPICS = "topics"
const val CATEGORY_PLAYLISTS = "playlists"
const val CATEGORY_WCCRM_SHEET_MUSIC = "wccrm_sheet_music"

// default sheet music category
val DEFAULT_SHEET_MUSIC_CATEGORY by lazy {
    buildCategoryUri(CATEGORY_WCCRM_SHEET_MUSIC, 0)
}

const val DEFAULT_CATEGORY_URI = "$SCHEME_NORMAL://$AUTHORITY/$CATEGORY_WCCRM/0"

/**
 * Matches a url string of the form content://hymnbook.com/{category}/{categoryId}
 *
 **/
const val CATEGORY_REGEX = """($SCHEME_HTTP|$SCHEME_NORMAL)://$AUTHORITY/(\w+)/(\d+)/?$"""

/**
 * Matches a url string of the form content://hymnbook.com/{category}/{categoryId}/hymns/{hymnId}
 */
const val ITEM_REGEX = """($SCHEME_HTTP|$SCHEME_NORMAL)://$AUTHORITY/(\w+)/(\d+)/hymns/(\d+)/?$"""

/**
 * Extracts hymnId from a hymnbook uri.
 * @return hymn id or null if uri is not a valid uri pointing to a hymn item
 */
fun String.hymnId(): String? {
    val itemRegex = ITEM_REGEX.toRegex()

    return if (itemRegex matches this) {
        val matchResult = itemRegex.find(this)
        matchResult?.groupValues?.get(4)
    } else null
}

fun String.isValidCategoryUri(): Boolean {
    val categoryRegex = CATEGORY_REGEX.toRegex()
    return categoryRegex matches this
}

/**
 * Uses string concatenation to build a simple category uri
 */
fun buildCategoryUri(category: String, categoryId: Int): String {
    return "$SCHEME_HTTP://$AUTHORITY/$category/$categoryId"
}

fun String.appendHymnId(hymnId: Int): String? {
    if (this.isValidCategoryUri()) {
        return "$this/hymns/$hymnId"
    }
    return null
}
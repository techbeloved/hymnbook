package com.techbeloved.hymnbook.utils

// content://hymnbook.com/{category}/{categoryId}/{hymns}/{hymnId}
const val AUTHORITY = "hymnbook.com"
const val SCHEME_NORMAL = "content"
const val SCHEME_HTTP = "https"

// Supported hymn listing categories
const val CATEGORY_WCCRM = "wccrm"
const val CATEGORY_TOPICS = "topics"
const val CATEGORY_PLAYLISTS = "playlists"
const val CATEGORY_WCCRM_SHEET_MUSIC = "wccrm_sheet_music"

/**
 * Matches a url string of the form content://hymnbook.com/{category}/{categoryId}
 *
 **/
const val CATEGORY_REGEX = """($SCHEME_HTTP|$SCHEME_NORMAL)://$AUTHORITY/(\w+)/(\d+)/{0,1}$"""

/**
 * Matches a url string of the form content://hymnbook.com/{category}/{categoryId}/hymns/{hymnId}
 */
const val ITEM_REGEX = """($SCHEME_HTTP|$SCHEME_NORMAL)://$AUTHORITY/(\w+)/(\d+)/hymns/(\d+)/{0,1}$"""

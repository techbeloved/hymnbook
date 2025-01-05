package com.techbeloved.hymnbook.shared.ext

import okio.Path

/**
 * Retrieve the extension of the file or empty
 * "sample3.mp3".extension() would return "mp3"
 * "sample3".extension() would return empty ""
 */
internal fun Path.extension(): String =
    name.substringAfterLast(delimiter = '.', missingDelimiterValue = "")

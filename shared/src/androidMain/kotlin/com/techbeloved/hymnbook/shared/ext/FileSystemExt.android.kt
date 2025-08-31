package com.techbeloved.hymnbook.shared.ext

import okio.Path
import okio.Path.Companion.toPath

internal actual fun absoluteFileUrl(relativePath: String): Path = relativePath.toPath()

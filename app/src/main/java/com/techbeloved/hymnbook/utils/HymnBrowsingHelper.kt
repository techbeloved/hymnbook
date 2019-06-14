package com.techbeloved.hymnbook.utils

import co.zsmb.verbalexpressions.VerEx
import ru.lanwen.verbalregex.VerbalExpression

// content://hymnbook.com/{category}/{categoryId}/{hymns}/{hymnId}
const val AUTHORITY = "hymnbook.com"
const val SCHEME_NORMAL = "content://"
const val SCHEME_HTTP = "https://"

val category = VerEx().startOfLine()
        .then(SCHEME_HTTP).or(SCHEME_NORMAL)
        .then(AUTHORITY)

val categoryRegex = VerbalExpression.regex()
        .startOfLine()
        .then(SCHEME_NORMAL).or(SCHEME_HTTP)
        .then(AUTHORITY)
        .then("/")
        .capt().word().endCapt() // Capture group 1: category name
        .then("/")
        .capt().digit().oneOrMore().endCapt() // Capture group 2: category id
        .endOfLine()
        .build()

/**
 * Matches a url string of the form content://hymnbook.com/{category}/{categoryId}/{hymns}/{hymnId}
 * Example usage: to the category: val catList =  itemInCategoryRex.getTextGroups(stringUrl, 1)
 **/
val itemInCategoryRex = VerbalExpression.regex()
        .startOfLine()
        .then(SCHEME_NORMAL).or(SCHEME_HTTP)
        .then(AUTHORITY)
        .then("/")
        .capt().word().endCapt() // Capture group 1: category name
        .then("/")
        .capt().digit().oneOrMore().endCapt() // Capture group 2: category id
        .then("/")
        .then("hymns")
        .then("/")
        .capt().digit().oneOrMore().endCapt() // Capture group 3: hymn id
        .endOfLine()
        .build()

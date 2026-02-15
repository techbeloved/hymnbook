package com.techbeloved.hymnbook.shared.search

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

internal class GetSearchSuggestionsUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    operator fun invoke(query: String) = if (query.isBlank()) flowOf(emptyList())
    else database.searchHistoryEntityQueries.getSearchSuggestions(query = query, limit = 5)
        .asFlow()
        .mapToList(dispatchersProvider.io())
        .map { suggestions ->
            suggestions.map { title ->
                extractQueryPlusNextWords(fullTitle = title, query = query, wordCount = 2)
            }
        }

    private fun extractQueryPlusNextWords(
        fullTitle: String,
        query: String,
        wordCount: Int = 1,
    ): String {
        val trimmedTitle = fullTitle.trim()

        // Find the index where the query match starts
        val startIndex = trimmedTitle.indexOf(query, ignoreCase = true)
        if (startIndex == -1) return trimmedTitle
        val afterQuery = trimmedTitle.substring(startIndex + query.length)

        val spaceAfterQuery = afterQuery.indexOf(' ')

        return if (spaceAfterQuery == -1) {
            trimmedTitle
        } else {
            val currentWordRemainder = afterQuery.substring(0, spaceAfterQuery)

            val restOfTitle = afterQuery.substring(spaceAfterQuery).trim()

            val nextWords = restOfTitle.split(Regex("\\s+"))
                .take(wordCount)
                .joinToString(" ")

            val result = trimmedTitle.substring(0, startIndex) +
                    query +
                    currentWordRemainder +
                    (if (nextWords.isNotEmpty()) " $nextWords" else "")

            result.trim()
        }
    }
}

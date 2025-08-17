package com.techbeloved.apiclient

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val NetworkTimeout = 20000L
internal expect fun platformHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient
private fun createHttpClient() = platformHttpClient {
    expectSuccess = true

    install(HttpCache)

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }

    install(HttpTimeout) {
        requestTimeoutMillis = NetworkTimeout
        connectTimeoutMillis = NetworkTimeout
        socketTimeoutMillis = NetworkTimeout
    }

    install(HttpCache)
}


class ApiClient {
    val httpClient by lazy { createHttpClient() }

    suspend inline fun <reified T> apiGet(
        url: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ) = httpClient.get(
        urlString = url,
        block = block,
    ).body<T>()
}

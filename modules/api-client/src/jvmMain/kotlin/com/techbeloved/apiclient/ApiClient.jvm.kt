package com.techbeloved.apiclient

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun platformHttpClient(
    block: HttpClientConfig<*>.() -> Unit
): HttpClient =
    HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
        }
        block()
    }

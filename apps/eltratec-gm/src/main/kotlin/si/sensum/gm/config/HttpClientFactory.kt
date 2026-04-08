package si.sensum.gm.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*

fun createHttpClient(timeoutMillis: Long): HttpClient {
    return HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMillis
            connectTimeoutMillis = timeoutMillis
            socketTimeoutMillis = timeoutMillis
        }

        expectSuccess = false
    }
}
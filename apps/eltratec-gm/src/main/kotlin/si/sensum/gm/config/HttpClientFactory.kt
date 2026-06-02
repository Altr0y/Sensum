package si.sensum.gm.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout

internal fun createHttpClient(timeoutMillis: Long): HttpClient {
    return HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = timeoutMillis
            connectTimeoutMillis = timeoutMillis
            socketTimeoutMillis = timeoutMillis
        }

        expectSuccess = false
    }
}
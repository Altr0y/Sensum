package si.sensum.backend

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import si.sensum.backend.config.loadBackendConfig
import si.sensum.backend.database.configureDatabases
import si.sensum.backend.plugins.configureBackendPlugins
import si.sensum.backend.plugins.configureBackendRouting

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val backendConfig = loadBackendConfig()

    configureDatabases()
    configureBackendPlugins()
    configureBackendRouting(backendConfig)
}
package com.twapps.serverstatuschecker

import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.routing.routing
import io.kvision.remote.applyRoutes
import io.kvision.remote.getAllServiceManagers
import io.kvision.remote.kvisionInit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.ktor.ext.get
import java.lang.Thread.sleep

val workerScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
val httpClient = HttpClient {  }

fun Application.main() {
    install(Compression)

    routing {
        getAllServiceManagers().forEach { applyRoutes(it) }
    }

    val module = module {
        single { ServerState(mutableListOf(), mutableMapOf(), StatusChecker(workerScope)) }

        factoryOf<StatusService>( { StatusService(get()) })
    }

    kvisionInit(module)

    Thread {
        while (true) {
            val state = get<ServerState>()
            state.checker.updateStatuses(state.urls, state.statuses)
            sleep(300_000)
        }
    }.start()
}


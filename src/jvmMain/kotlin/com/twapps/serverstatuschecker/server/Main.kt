package com.twapps.serverstatuschecker.server

import com.twapps.serverstatuschecker.server.config.StorageConfig
import com.twapps.serverstatuschecker.services.IStatusService
import com.twapps.serverstatuschecker.services.StatusChecker
import com.twapps.serverstatuschecker.services.StatusService
import com.twapps.util.FileUtil.child
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.origin
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.kvision.remote.applyRoutes
import io.kvision.remote.getAllServiceManagers
import io.kvision.remote.getServiceManager
import io.kvision.remote.kvisionInit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.ktor.ext.get
import java.io.File
import java.lang.Thread.sleep

val storageConfig = StorageConfig.default(File("data"))
val workerScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

fun Application.main() {
    install(Compression)

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }

        for (statusCode in HttpStatusCode.allStatusCodes) {
            status(statusCode) { call, status ->
                call.respondText(text = "${status.value}, ${status.description}", status = statusCode)
            }
        }
    }

    install(CallLogging) {
        // output file is configured via logback.xml
        //this.disableForStaticContent()
        this.format { call ->
            "[${call.request.httpMethod.value}] ${call.response.status()?.value} to ${call.request.path()} from ${call.request.origin.remoteHost}:${call.request.origin.remotePort}"
        }
    }

    val module = module {
        single { ServerState(mutableListOf(), mutableMapOf(), StatusChecker(workerScope)) }

        factoryOf<StatusService>( { StatusService(get()) })
    }

    routing {
        applyRoutes(getServiceManager<IStatusService>())
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


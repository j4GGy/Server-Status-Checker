package com.twapps.serverstatuschecker

import io.kvision.remote.getService

object Model {
    private val statusService = getService<IStatusService>()

    suspend fun getStatusList(): Failable<List<Pair<String, ServerStatus>>> {
        return tryOrFailSuspend { statusService.getStatusList() }
    }

    suspend fun addUrl(url: String): Failable<String> {
        return tryOrFailFailable { statusService.addUrl(url) }
    }
}

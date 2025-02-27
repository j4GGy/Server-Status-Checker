package com.twapps.serverstatuschecker

import com.twapps.serverstatuschecker.services.IStatusService
import com.twapps.serverstatuschecker.services.ServerStatus
import com.twapps.serverstatuschecker.util.Failable
import com.twapps.serverstatuschecker.util.tryOrFailSuspend
import io.kvision.remote.getService

object Model {
    private val statusService = getService<IStatusService>()

    suspend fun getStatusList(): Failable<List<Pair<String, ServerStatus>>> {
        return tryOrFailSuspend { statusService.getStatusList() }
    }

    suspend fun addUrls(urls: List<String>, addVariations: Boolean): Failable<List<String>> {
        return tryOrFailSuspend { statusService.addUrls(urls, addVariations) }
    }

    suspend fun addUrl(url: String, addVariations: Boolean): Failable<String> {
        return tryOrFailSuspend { statusService.addUrl(url, addVariations) }
    }

    suspend fun removeUrl(url: String): Failable<String> {
        return tryOrFailSuspend { statusService.removeUrl(url) }
    }
}

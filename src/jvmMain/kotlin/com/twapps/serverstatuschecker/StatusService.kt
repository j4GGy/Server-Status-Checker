package com.twapps.serverstatuschecker

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual class StatusService constructor(
    private val serverState: ServerState
) : IStatusService {

    override suspend fun addUrl(url: String): String {
        println("addUrl called with url=[$url]")

        val validUrl = makeUrlValid(url)
        if (serverState.urls.contains(validUrl)) {
            println("$validUrl already exists")
            throw IllegalArgumentException("$validUrl already exists")
        } else {
            println("Adding $validUrl")
            serverState.urls.add(validUrl)
            serverState.checker.updateStatuses(serverState.urls, serverState.statuses)
            return validUrl
        }
    }

    override suspend fun getStatusList(): List<Pair<String, ServerStatus>> {
        return serverState.statuses.toList()
    }

    override suspend fun removeUrl(url: String): String {
        println("removeUrl called with url=[$url]")
        if (url in serverState.urls) {
            println("Removing $url")
            serverState.urls.remove(url)
            serverState.statuses.remove(url)
            return url
        } else {
            println("Cannot remove $url")
            throw IllegalArgumentException("Cannot remove $url")
        }
    }

    private fun makeUrlValid(url: String): String {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return makeUrlValid("http://$url")
        }
        return url
    }
}
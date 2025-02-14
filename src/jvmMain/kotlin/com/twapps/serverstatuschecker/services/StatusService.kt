package com.twapps.serverstatuschecker.services

import com.twapps.serverstatuschecker.server.ServerState
import com.twapps.serverstatuschecker.util.tryOrNull

@Suppress("ACTUAL_WITHOUT_EXPECT")
actual class StatusService constructor(
    private val serverState: ServerState
) : IStatusService {

    override suspend fun addUrl(url: String, addVariations: Boolean): String {
        println("addUrl called with url=[$url], addVariations=[$addVariations]")
        if (addVariations) {
            val urls = createVariations(url)
            urls.forEach { addUrl(it, false) }
            return url
        }

        val validUrl = makeUrlValid(url)
        if (serverState.urls.contains(validUrl)) {
            println("$validUrl already exists")
            throw IllegalArgumentException("$validUrl already exists")
        } else {
            println("Adding $validUrl")
            serverState.urls.add(validUrl)
            serverState.checker.updateStatus(validUrl, serverState.statuses)
            return validUrl
        }
    }

    override suspend fun addUrls(urls: List<String>, addVariations: Boolean): List<String> {
        println("addUrls called with urls=[$urls]")
        return urls.mapNotNull { url ->
            tryOrNull { addUrl(url, addVariations) }
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

    private fun createVariations(url: String): List<String> {
        val rootDomain = url.substringAfter("://").removePrefix("www.")

        return listOf(
            rootDomain,
            "www.$rootDomain",
        ).flatMap { listOf("http://$it", "https://$it") }
    }
}
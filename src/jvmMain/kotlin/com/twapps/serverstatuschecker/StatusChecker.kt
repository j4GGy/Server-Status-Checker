package com.twapps.serverstatuschecker

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class StatusChecker constructor(
    private val scope: CoroutineScope,
) {
    companion object {
        val expectedResponseOk = HttpStatusCode.OK.toResponse()

        private fun HttpStatusCode.toResponse() = Response(value, description)
        private fun Exception.toOutputString() = this::class.simpleName + ": " + message
    }

    fun updateStatuses(urls: List<String>, statusList: MutableMap<String, ServerStatus>) {
        println("Updating statuses")
        urls.forEach { updateStatus(it, statusList) }
    }

    fun updateStatus(url: String, statusList: MutableMap<String, ServerStatus>) {
        scope.launch {
            println("Checking $url")
            try {
                val response = httpClient.get(url)
                println("Got response from $url: $response")
                statusList[url] = ServerStatus(response.status.toResponse(), null, expectedResponseOk)
            } catch (exception: Exception) {
                statusList[url] = ServerStatus(null, exception.toOutputString(), expectedResponseOk)
                println("Could not connect to $url: $exception")
            }
        }
    }
}
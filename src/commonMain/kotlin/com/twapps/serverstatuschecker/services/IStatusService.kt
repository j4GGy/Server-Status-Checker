package com.twapps.serverstatuschecker.services

import io.kvision.annotations.KVService
import kotlinx.serialization.Serializable

@KVService
interface IStatusService {
    suspend fun addUrl(url: String, addVariations: Boolean): String
    suspend fun addUrls(urls: List<String>, addVariations: Boolean): List<String>
    suspend fun getStatusList(): List<Pair<String, ServerStatus>>
    suspend fun removeUrl(url: String): String
}

@Serializable
class ServerStatus constructor(
    val response: Response?,
    val error: String?,
    val expectedResponse: Response,
) {
    fun isOk() = response?.code == expectedResponse.code
    fun isUnexpected() = response != null && response.code != expectedResponse.code
    fun isError() = error != null
}

@Serializable
class Response constructor(
    val code: Int,
    val message: String,
) {
    override fun toString(): String = "$code: $message"
}
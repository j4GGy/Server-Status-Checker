package com.twapps.serverstatuschecker

class ServerState constructor(
    val urls: MutableList<String>,
    val statuses: MutableMap<String, ServerStatus> = mutableMapOf(),
    val checker: StatusChecker
)
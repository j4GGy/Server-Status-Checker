package com.twapps.serverstatuschecker.server

import com.twapps.serverstatuschecker.services.StatusChecker
import com.twapps.serverstatuschecker.services.ServerStatus

class ServerState constructor(
    val urls: MutableList<String>,
    val statuses: MutableMap<String, ServerStatus> = mutableMapOf(),
    val checker: StatusChecker
)
package com.twapps.serverstatuschecker.server.config

import com.twapps.util.FileUtil.child
import com.twapps.util.FileUtil.ensureDirectoryExists
import java.io.File

class StorageConfig constructor(
    /**
     * Directory which is NOT deleted when re-starting / overwriting the docker image.
     * -> Data stored here is persistent.
     */
    val data: File,
    val database: File,
    val logs: File,
    val staticContent: File,
) {
    companion object {
        fun default(dataDirectory: File) = StorageConfig(
            dataDirectory,
            dataDirectory.child("databases"),
            dataDirectory.child("log"),
            dataDirectory.child("static")
        )
    }

    init {
        println("Data directory: ${data.absolutePath}")

        data.ensureDirectoryExists()
        database.ensureDirectoryExists()
        logs.ensureDirectoryExists()
        staticContent.ensureDirectoryExists()
    }
}
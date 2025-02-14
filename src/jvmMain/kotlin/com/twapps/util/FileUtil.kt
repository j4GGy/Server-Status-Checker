package com.twapps.util

import java.io.File

object FileUtil {
    fun File.child(name: String) = File(this, name)
    fun File.ensureDirectoryExists(): Boolean {
        if (exists()) return true

        return if (!mkdirs()) {
            System.err.println("ERROR: Failed to create directory: $this")
            false
        } else {
            true
        }
    }
}
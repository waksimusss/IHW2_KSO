package restaurant

import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

internal object Logger {
    enum class Status {
        OK,
        ERROR
    }

    init {
        if (!Files.exists(Paths.get("./data"))) {
            Files.createDirectory(Paths.get("./data"))
        }
    }

    private var pathName = "data/logs.log"
    private var lock = Any()

    fun writeToLog(text: String) {
        try {
            synchronized(lock) {
                FileWriter(pathName, true).use {
                    it.write("[${Date()}]. $text. \n")
                }
            }
        } catch (_: Exception) {
        }
    }

    fun writeToLogResult(text: String, status : Status) {
        try {
            synchronized(lock) {
                FileWriter(pathName, true).use {
                    it.write("[${Date()}]. $text. Status: $status \n")
                }
            }
        } catch (_: Exception) {
        }
    }
}
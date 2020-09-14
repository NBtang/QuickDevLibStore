package com.laotang.quickdev.logcat

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import com.orhanobut.logger.DiskLogStrategy
import java.io.File
import java.io.FileWriter
import java.io.IOException

class LogcatLogDiskStrategy private constructor(handler: Handler) : DiskLogStrategy(handler) {

    companion object {
        const val MAX_BYTES = 100 * 1024
        private var logcatLoggerFilePath = ""

        fun getLogcatLoggerFilePath(): String {
            return logcatLoggerFilePath
        }

        fun getLogcatLogDiskStrategy(
            context: Context,
            logFile: File? = null,
            maxFileSize: Int = MAX_BYTES
        ): LogcatLogDiskStrategy {
            val folder = if (logFile == null) {
                val diskDir = File(context.externalCacheDir, "log")
                if (!diskDir.exists()) {
                    diskDir.mkdir()
                }
                File(diskDir, "logger").absolutePath
            } else {
                if(!logFile.exists())
                    logFile.mkdir()
                logFile.absolutePath
            }
            logcatLoggerFilePath = folder
            val ht = HandlerThread("AndroidFileLogger.$folder")
            ht.start()
            val handel = WriteHandler(
                ht.looper,
                folder,
                maxFileSize
            )
            return LogcatLogDiskStrategy(handel)
        }

        class WriteHandler(
            looper: Looper,
            folder: String,
            maxFileSize: Int
        ) :
            Handler(looper) {
            private val folder: String
            private val maxFileSize: Int
            override fun handleMessage(msg: Message) {
                var content = msg.obj as String
                if (!content.endsWith("\n")) {
                    content += "\n"
                }
                var fileWriter: FileWriter? = null
                val logFile = getLogFile(folder, "logs")
                try {
                    fileWriter = FileWriter(logFile, true)
                    writeLog(fileWriter, content)
                    fileWriter.flush()
                    fileWriter.close()
                } catch (e: IOException) {
                    if (fileWriter != null) {
                        try {
                            fileWriter.flush()
                            fileWriter.close()
                        } catch (e1: IOException) {

                        }
                    }
                }
            }

            @Throws(IOException::class)
            private fun writeLog(
                fileWriter: FileWriter,
                content: String
            ) {
                fileWriter.append(content)
            }

            private fun getLogFile(
                folderName: String,
                fileName: String
            ): File {
                val folder = File(folderName)
                if (!folder.exists()) {
                    folder.mkdirs()
                }
                var newFileCount = 0
                var newFile: File
                var existingFile: File? = null
                newFile =
                    File(folder, String.format("%s_%s.log", fileName, newFileCount))
                while (newFile.exists()) {
                    existingFile = newFile
                    newFileCount++
                    newFile = File(
                        folder,
                        String.format("%s_%s.log", fileName, newFileCount)
                    )
                }
                return if (existingFile != null) {
                    if (existingFile.length() >= maxFileSize) {
                        newFile
                    } else {
                        existingFile
                    }
                } else newFile
            }

            init {
                this.folder = folder
                this.maxFileSize = maxFileSize
            }
        }
    }
}
package com.laotang.quickdev.logcat

import android.content.Context
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.LogAdapter
import com.orhanobut.logger.LogStrategy
import com.orhanobut.logger.PrettyFormatStrategy
import java.io.File

class LogcatLoggerAdapter private constructor(private val formatStrategy: FormatStrategy) :
    LogAdapter {

    override fun isLoggable(priority: Int, tag: String?): Boolean {
        return true
    }

    override fun log(priority: Int, tag: String?, message: String) {
        formatStrategy.log(priority, tag, message)
    }

    class Builder(context: Context) {
        private val applicationContext = context.applicationContext
        private var logFilePath: String? = null
        private var maxFileSize: Int = LogcatLogDiskStrategy.MAX_BYTES
        private var formatStrategy: FormatStrategy? = null

        fun setLogFilePath(logFilePath: String): Builder {
            this.logFilePath = logFilePath
            return this
        }

        fun setMaxFileSize(maxFileSize: Int): Builder {
            if (maxFileSize <= 0) {
                return this
            }
            this.maxFileSize = maxFileSize
            return this
        }

        fun setFormatStrategy(formatStrategy: FormatStrategy): Builder {
            this.formatStrategy = formatStrategy
            return this
        }

        fun build(): LogcatLoggerAdapter {
            if (formatStrategy == null) {
                formatStrategy = PrettyFormatStrategy
                    .newBuilder()
                    .tag("logcatDisk")
                    .logStrategy(
                        LogcatLogDiskStrategy.getLogcatLogDiskStrategy(
                            context = applicationContext,
                            logFile = if (logFilePath == null) null else File(logFilePath),
                            maxFileSize = maxFileSize
                        )
                    )
                    .build()
            }
            return LogcatLoggerAdapter(formatStrategy!!)
        }
    }
}
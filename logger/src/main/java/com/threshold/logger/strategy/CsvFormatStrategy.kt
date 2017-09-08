package com.threshold.logger.strategy

import android.os.Environment
import android.os.HandlerThread
import com.threshold.logger.util.Utils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CsvFormatStrategy(private val methodCount: Int, private val methodOffset: Int,
                        private val date: Date, private val dateFormat: SimpleDateFormat,
                        private val logStrategy: LogStrategy, private val tag: String) : FormatStrategy {

    companion object {
        inline fun build(block: Builder.() -> Unit): CsvFormatStrategy {
            return Builder().apply(block).build()
        }
    }

    private constructor(builder: Builder) : this(builder.methodCount, builder.methodOffset, Date(), builder.dateFormat!!, builder.logStrategy!!, builder.tag)

    private val NEW_LINE = System.getProperty("line.separator")
    private val DOUBLE_QUOTES = "\""
    private val APOSTROPHE = "'"
    //    private val NEW_LINE_REPLACEMENT = " <br> "
    private val SEPARATOR = ","

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun log(priority: Int, onceOnlyTag: String?, message: String?) {
        val tag = Utils.formatTag(tag, onceOnlyTag)
        date.time = System.currentTimeMillis()

        val builder = StringBuilder()
        with(builder) {
            // machine-readable date/time
            append(date.time)

            // human-readable date/time
            append(SEPARATOR)
            append(dateFormat.format(date))

            // level
            append(SEPARATOR)
            append(Utils.logLevel(priority))

            //tag
            append(SEPARATOR)
            append(tag)

            //code location
            append(SEPARATOR)
            var myMethodCount = methodCount
            val trace = Thread.currentThread().stackTrace
            var level = ""
            val stackOffset = Utils.getStackOffset(trace) + methodOffset

            //corresponding method count with the current stack may exceeds the stack trace. Trims the count
            if (myMethodCount + stackOffset > trace.size) {
                myMethodCount = trace.size - stackOffset - 1
            }

            append(DOUBLE_QUOTES)
            for (i in myMethodCount downTo 1) {
                val stackIndex = i + stackOffset
                if (stackIndex >= trace.size) {
                    continue
                }
                append(level)
                append(Utils.getSimpleClassName(trace[stackIndex].className))
                append(".")
                append(trace[stackIndex].methodName)
                append(" ")
                append(" (")
                append(trace[stackIndex].fileName)
                append(":")
                append(trace[stackIndex].lineNumber)
                append(")")
                append(NEW_LINE)
                level += "   "
            }
            append(DOUBLE_QUOTES)

            //message
            message?.let {
                append(SEPARATOR)
                if (message.contains(DOUBLE_QUOTES) || message.contains(SEPARATOR) || message.contains(NEW_LINE)) {
                    append("$DOUBLE_QUOTES${it.replace(DOUBLE_QUOTES, APOSTROPHE)}$DOUBLE_QUOTES")
                } else {
                    append(it)
                }
            }
            // new line
            append(NEW_LINE)
        }
        logStrategy.log(priority, tag, builder.toString())
    }

    class Builder {
        var methodCount = 2
        var methodOffset = 0
        var dateFormat: SimpleDateFormat? = null
        /**
         * This folder is for [DiskLogStrategy.WriteHandler] to use.We will record log file at this folder.
         * Note: If you provide [logStrategy], we will ignore this log folder config, because in [LogStrategy], you can config anything if you want
         */
        var logFolder: String? = null
        var logStrategy: LogStrategy? = null
        var tag = "PRETTY_LOGGER"

        fun build(): CsvFormatStrategy {
            if (dateFormat == null) {
                dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINESE)
            }
            if (logStrategy == null) {
                if (logFolder == null) {
                    logFolder = Environment.getExternalStorageDirectory().absolutePath + File.separatorChar + "PrettyLogger"
                }
                val ht = HandlerThread("AndroidFileLogger." + logFolder)
                ht.start()
                val handler = DiskLogStrategy.WriteHandler(ht.looper, logFolder!!, MAX_BYTES)
                logStrategy = DiskLogStrategy(handler)
            }
            return CsvFormatStrategy(this)
        }

        companion object {
            const val MAX_BYTES = 500 * 1024 // 500K averages to a 4000 lines per file
        }
    }
}
package com.threshold.logger.strategy

import com.threshold.logger.util.Utils

class PrettyFormatStrategy(private val methodCount: Int, private val methodOffset: Int,
                           private val showThreadInfo: Boolean, private val logStrategy: LogStrategy,
                           private val tag: String) : FormatStrategy {

    companion object {
        fun build(block: Builder.() -> Unit): PrettyFormatStrategy {
            return Builder().apply(block).build()
        }
    }

    private constructor(builder: Builder) : this(builder.methodCount, builder.methodOffset, builder.showThreadInfo, builder.logStrategy!!, builder.tag)

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    private val CHUNK_SIZE = 4000

    /**
     * Drawing toolbox
     */
    private val TOP_LEFT_CORNER = '┌'
    private val BOTTOM_LEFT_CORNER = '└'
    private val MIDDLE_CORNER = '├'
    private val HORIZONTAL_LINE = '│'
    private val DOUBLE_DIVIDER = "────────────────────────────────────────────────────────"
    private val SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
    private val TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private val BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private val MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun log(priority: Int, onceOnlyTag: String?, message: String?) {
        val tag = Utils.formatTag(tag, onceOnlyTag)

        logTopBorder(priority, tag)
        logHeaderContent(priority, tag, methodCount)

        //get bytes of message with system's default charset (which is UTF-8 for Android)
        val bytes = message?.toByteArray() ?: ByteArray(0)
        val length = bytes.size
        if (length <= CHUNK_SIZE) {
            if (methodCount > 0) {
                logDivider(priority, tag)
            }
            logContent(priority, tag, message)
            logBottomBorder(priority, tag)
            return
        }
        if (methodCount > 0) {
            logDivider(priority, tag)
        }
        var i = 0
        while (i < length) {
            val count = Math.min(length - i, CHUNK_SIZE)
            //create a new String with system's default charset (which is UTF-8 for Android)
            logContent(priority, tag, String(bytes, i, count))
            i += CHUNK_SIZE
        }
        logBottomBorder(priority, tag)
    }

    private fun logTopBorder(logType: Int, tag: String) {
        logChunk(logType, tag, TOP_BORDER)
    }

    private fun logHeaderContent(logType: Int, tag: String, methodCount: Int) {
        var myMethodCount = methodCount
        val trace = Thread.currentThread().stackTrace
        if (showThreadInfo) {
            logChunk(logType, tag, HORIZONTAL_LINE + " Thread: " + Thread.currentThread().name)
            logDivider(logType, tag)
        }
        var level = ""

        val stackOffset = Utils.getStackOffset(trace) + methodOffset

        //corresponding method count with the current stack may exceeds the stack trace. Trims the count
        if (myMethodCount + stackOffset > trace.size) {
            myMethodCount = trace.size - stackOffset - 1
        }

        for (i in myMethodCount downTo 1) {
            val stackIndex = i + stackOffset
            if (stackIndex >= trace.size) {
                continue
            }
            val builder = StringBuilder()
            builder.append(HORIZONTAL_LINE)
                    .append(' ')
                    .append(level)
                    .append(Utils.getSimpleClassName(trace[stackIndex].className))
                    .append(".")
                    .append(trace[stackIndex].methodName)
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].fileName)
                    .append(":")
                    .append(trace[stackIndex].lineNumber)
                    .append(")")
            level += "   "
            logChunk(logType, tag, builder.toString())
        }
    }

    private fun logBottomBorder(logType: Int, tag: String) {
        logChunk(logType, tag, BOTTOM_BORDER)
    }

    private fun logDivider(logType: Int, tag: String) {
        logChunk(logType, tag, MIDDLE_BORDER)
    }

    private fun logContent(logType: Int, tag: String?, chunk: String?) {
        val lines = chunk?.split(System.getProperty("line.separator").toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        lines?.forEach {
            logChunk(logType, tag,   "$HORIZONTAL_LINE $it")
        }
    }

    private fun logChunk(priority: Int, tag: String?, chunk: String) {
        logStrategy.log(priority, tag, chunk)
    }

    class Builder {
        var methodCount = 2
        var methodOffset = 0
        var showThreadInfo = true
        var logStrategy: LogStrategy? = null
        var tag = "PRETTY_LOGGER"

        fun build(): PrettyFormatStrategy {
            if (logStrategy == null) {
                logStrategy = LogcatLogStrategy()
            }
            return PrettyFormatStrategy(this)
        }
    }
}
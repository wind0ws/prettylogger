package com.threshold.logger.util

import com.threshold.logger.PrettyLogger
import com.threshold.logger.printer.LoggerPrinter
import com.threshold.logger.printer.Printer
import java.io.PrintWriter
import java.io.StringWriter
import java.net.UnknownHostException
import java.util.*

object Utils {

    private val MIN_STACK_OFFSET = 5

    /**
     * Copied from "android.util.Log.getStackTraceString()" in order to avoid usage of Android stack
     * in unit tests.
     *
     * @return Stack trace in form of String
     */
    fun getStackTraceString(tr: Throwable?): String {
        if (tr == null) {
            return ""
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        var t = tr
        while (t != null) {
            if (t is UnknownHostException) {
                return ""
            }
            t = t.cause
        }

        val sw = StringWriter()
        val pw = PrintWriter(sw)
        tr.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

    fun logLevel(priority: Int): String {
        return when (priority) {
            android.util.Log.VERBOSE -> "VERBOSE"
            android.util.Log.DEBUG -> "DEBUG"
            android.util.Log.INFO -> "INFO"
            android.util.Log.WARN -> "WARN"
            android.util.Log.ERROR -> "ERROR"
            android.util.Log.ASSERT -> "ASSERT"
            else -> "UNKNOWN"
        }
    }

    fun toString(obj: Any?): String {
        if (obj == null) {
            return "null"
        }
        if (!obj.javaClass.isArray) {
            return obj.toString()
        }
        if (obj is IntArray) {
            return Arrays.toString(obj as IntArray?)
        }
        if (obj is LongArray) {
            return Arrays.toString(obj as LongArray?)
        }
        if (obj is DoubleArray) {
            return Arrays.toString(obj as DoubleArray?)
        }
        if (obj is FloatArray) {
            return Arrays.toString(obj as FloatArray?)
        }
        if (obj is BooleanArray) {
            return Arrays.toString(obj as BooleanArray?)
        }
        if (obj is ByteArray) {
            return Arrays.toString(obj as ByteArray?)
        }
        if (obj is CharArray) {
            return Arrays.toString(obj as CharArray?)
        }
        if (obj is ShortArray) {
            return Arrays.toString(obj as ShortArray?)
        }
        return if (obj is Array<*>) {
            Arrays.deepToString(obj as Array<*>?)
        } else "Couldn't find a correct type for the array $obj"
    }

    /**
     * Determines the starting index of the stack trace, after method calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    fun getStackOffset(trace: Array<StackTraceElement>): Int {
        var i = MIN_STACK_OFFSET
        val printerName = Printer::class.java.name
        val loggerPrinterName = LoggerPrinter::class.java.name
        val beautyLoggerName = PrettyLogger::class.java.name
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
//            if (name != loggerPrinterName && name != "${Printer::class.java.name}\$DefaultImpls" &&
//                    name != beautyLoggerName && name != "${beautyLoggerName}Kt") {
//                return --i
//            }
            if (!name.startsWith(loggerPrinterName) &&
                    !name.startsWith(printerName) &&
                    !name.startsWith(beautyLoggerName)) {
                return --i
            }
            i++
        }
        return -1
    }

    fun formatTag(defaultTag: String, passedInTag: String?): String {
        return if (!passedInTag.isNullOrEmpty() && defaultTag!=passedInTag) {
            defaultTag + "-" + passedInTag
        } else defaultTag
    }

    fun getSimpleClassName(name: String): String {
        val lastIndex = name.lastIndexOf(".")
        return name.substring(lastIndex + 1)
    }

}
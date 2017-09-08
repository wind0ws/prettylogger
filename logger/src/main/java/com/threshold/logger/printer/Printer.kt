package com.threshold.logger.printer

import com.threshold.logger.adapter.LogAdapter

interface Printer {
    fun addAdapter(adapter: LogAdapter)

    fun tag(tag: String?): Printer

    fun debug(message: Any?, throwable: Throwable? = null)

    fun debug(throwable: Throwable? = null, message: () -> Any?)

    fun error(message: Any?, throwable: Throwable? = null)

    fun error(throwable: Throwable? = null, message: () -> Any?)

    fun warn(message: Any?, throwable: Throwable? = null)

    fun warn(throwable: Throwable? = null, message: () -> Any?)

    fun info(message: Any?)

    fun info(message: () -> Any?)

    fun verbose(message: Any?)

    fun verbose(message: () -> Any?)

    fun wtf(message: Any?, throwable: Throwable? = null)

    fun wtf(throwable: Throwable? = null, message: () -> Any?)

    /**
     * Formats the given debugJson content and print it
     */
    fun debugJson(json: String?)

    fun debugJson(json: () -> String?)

    /**
     * Formats the given debugXml content and print it
     */
    fun debugXml(xml: String?)

    fun debugXml(xml: () -> String?)

    fun log(priority: Int, tag: String? = null, message: Any?, throwable: Throwable? = null)

    fun log(priority: Int, tag: String? = null, throwable: Throwable? = null, message: () -> Any?)

    fun clearLogAdapters()
}
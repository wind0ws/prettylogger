package com.threshold.logger

import com.threshold.logger.adapter.LogAdapter
import com.threshold.logger.printer.LoggerPrinter
import com.threshold.logger.printer.Printer

interface PrettyLogger {
    /**
     * The logger tag used in extension functions for the [PrettyLogger].
     * Note that the tag length should not be more than 23 symbols.
     */
    val loggerTag: String
        get() = getTag(javaClass)
}

object PrinterHolder {
    var printer: Printer = LoggerPrinter()
}

fun PrettyLogger.getPrinter(): Printer {
    return PrinterHolder.printer
}

fun PrettyLogger.setPrinter(printer: Printer) {
    PrinterHolder.printer = printer
}

fun PrettyLogger.addAdapter(adapter: LogAdapter) {
    PrinterHolder.printer.addAdapter(adapter)
}

fun PrettyLogger.clearAdapters() {
    PrinterHolder.printer.clearLogAdapters()
}

/**
 * The tag for the coming log.
 * Note: this tag is used once only. For every log tag, you should config
 */
fun PrettyLogger.tag(tag: String?): Printer {
    return PrinterHolder.printer.tag(tag)
}

fun PrettyLogger.debug(message: Any?, throwable: Throwable? = null) {
    PrinterHolder.printer.debug(message, throwable)
}

fun PrettyLogger.verbose(message: Any?) {
    PrinterHolder.printer.verbose(message)
}

fun PrettyLogger.info(message: Any?) {
    PrinterHolder.printer.info(message)
}

fun PrettyLogger.warn(message: Any?, throwable: Throwable? = null) {
    PrinterHolder.printer.warn(message, throwable)
}

fun PrettyLogger.error(message: Any?, throwable: Throwable? = null) {
    PrinterHolder.printer.error(message, throwable)
}

fun PrettyLogger.wtf(message: Any?, throwable: Throwable? = null) {
    PrinterHolder.printer.wtf(message, throwable)
}

fun PrettyLogger.debugJson(json: String?) {
    PrinterHolder.printer.debugJson(json)
}

fun PrettyLogger.debugXml(xml: String?) {
    PrinterHolder.printer.debugXml(xml)
}

fun PrettyLogger.verbose(message: () -> Any?) {
    PrinterHolder.printer.verbose(message)
}

fun PrettyLogger.debug(throwable: Throwable? = null, message: () -> Any?) {
    PrinterHolder.printer.debug(throwable, message)
}

fun PrettyLogger.info(message: () -> Any?) {
    PrinterHolder.printer.info(message)
}

fun PrettyLogger.warn(throwable: Throwable? = null, message: () -> Any?) {
    PrinterHolder.printer.warn(throwable, message)
}

fun PrettyLogger.error(throwable: Throwable? = null, message: () -> Any?) {
    PrinterHolder.printer.error(throwable, message)
}

fun PrettyLogger.wtf(throwable: Throwable? = null, message: () -> Any?) {
    PrinterHolder.printer.wtf(throwable, message)
}

fun PrettyLogger.debugJson(json: () -> String?) {
    PrinterHolder.printer.debugJson(json)
}

fun PrettyLogger.debugXml(xml: () -> String?) {
    PrinterHolder.printer.debugXml(xml)
}

private fun getTag(clazz: Class<*>): String {
    val tag = clazz.simpleName
    return if (tag.length <= 23) {
        tag
    } else {
        tag.substring(0, 23)
    }
}
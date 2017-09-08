package com.threshold.logger.printer

import android.util.Log.*
import com.threshold.logger.adapter.LogAdapter
import com.threshold.logger.util.Utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

class LoggerPrinter : Printer {

    companion object {
        /**
         * It is used for debugJson pretty print
         */
        private val JSON_INDENT = 2
    }

    override fun debug(message: Any?, throwable: Throwable?) {
        log(DEBUG, null, message, throwable)
    }

    override fun debug(throwable: Throwable?, message: () -> Any?) {
        log(DEBUG, null, throwable, message)
    }

    override fun error(message: Any?, throwable: Throwable?) {
        log(ERROR, null, message, throwable)
    }

    override fun error(throwable: Throwable?, message: () -> Any?) {
        log(ERROR, null, throwable, message)
    }

    override fun warn(message: Any?, throwable: Throwable?) {
        log(WARN, null, message, throwable)
    }

    override fun warn(throwable: Throwable?, message: () -> Any?) {
        log(WARN, null, throwable, message)
    }

    override fun info(message: Any?) {
        log(INFO, null, Utils.toString(message))
    }

    override fun info(message: () -> Any?) {
        log(INFO, null, null, message)
    }

    override fun verbose(message: Any?) {
        log(VERBOSE, null, message)
    }

    override fun verbose(message: () -> Any?) {
        log(VERBOSE, null, null, message)
    }

    override fun wtf(message: Any?, throwable: Throwable?) {
        log(ASSERT, null, message, throwable)
    }

    override fun wtf(throwable: Throwable?, message: () -> Any?) {
        log(ASSERT, null, throwable, message)
    }


    /**
     * Provides one-time used tag for the log message
     */
    private val localTag = ThreadLocal<String>()

    private val logAdapters = ArrayList<LogAdapter>()

    override fun tag(tag: String?): Printer {
        if (tag != null) {
            localTag.set(tag)
        }
        return this
    }

    override fun debugJson(json: String?) {
        if (!isLoggable(DEBUG)) {
            return
        }
        if (json.isNullOrEmpty()) {
            debug("Empty/Null json content")
            return
        }
        try {
            val myJson = json!!.trim { it <= ' ' }
            if (myJson.startsWith("{")) {
                val jsonObject = JSONObject(myJson)
                val message = jsonObject.toString(JSON_INDENT)
                debug(message)
                return
            }
            if (myJson.startsWith("[")) {
                val jsonArray = JSONArray(myJson)
                val message = jsonArray.toString(JSON_INDENT)
                debug(message)
                return
            }
            error("Invalid Json:$json")
        } catch (e: JSONException) {
            error("Invalid Json:$json")
        }
    }

    override fun debugJson(json: () -> String?) {
        if (!isLoggable(DEBUG)) {
            return
        }
        debugJson(json())
    }

    override fun debugXml(xml: String?) {
        if (!isLoggable(DEBUG)) {
            return
        }
        if (xml.isNullOrEmpty()) {
            debug("Empty/Null xml content")
            return
        }
        try {
            val xmlInput = StreamSource(StringReader(xml))
            val xmlOutput = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            with(transformer) {
                setOutputProperty(OutputKeys.INDENT, "yes")
                setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
                transform(xmlInput, xmlOutput)
            }
            debug(xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n"))
        } catch (e: TransformerException) {
            error("Invalid xml")
        }
    }

    override fun debugXml(xml: () -> String?) {
        if (!isLoggable(DEBUG)) {
            return
        }
        debugXml(xml())
    }

    @Synchronized override fun log(priority: Int, tag: String?, message: Any?, throwable: Throwable?) {
        var msg: String? = null
        val usingTag = tag ?: getTag()
        logAdapters
                .filter { it.isLoggable(priority, usingTag) }
                .apply {
                    if (msg == null) {
                        msg = composeMessage(message, throwable)
                    }
                }
                .forEach { it.log(priority, usingTag, msg) }
    }

    @Synchronized override fun log(priority: Int, tag: String?, throwable: Throwable?, message: () -> Any?) {
        var msg: String? = null
        val usingTag = tag ?: getTag()
        logAdapters
                .filter { it.isLoggable(priority, usingTag) }
                .apply {
                    if (msg == null) {
                        msg = composeMessage(message, throwable)
                    }
                }
                .forEach { it.log(priority, usingTag, msg) }
    }

    private fun composeMessage(message: Any?, throwable: Throwable?): String {
        return composeMessage({ message }, throwable)
    }

    private inline fun composeMessage(message: () -> Any?, throwable: Throwable?): String {
        return buildString {
            append(Utils.toString(message()))
            throwable?.let {
                append(" : ")
                append(Utils.getStackTraceString(it))
            }
        }
    }

    override fun clearLogAdapters() {
        logAdapters.clear()
    }

    override fun addAdapter(adapter: LogAdapter) {
        logAdapters.add(adapter)
    }

    /**
     * @return the appropriate tag based on local or global
     */
    private fun getTag(): String? {
        val tag = localTag.get()
        if (tag != null) {
            localTag.remove()
            return tag
        }
        return null
    }


    private fun isLoggable(priority: Int, tag: String? = null): Boolean {
        val myTag = tag ?: localTag.get()
        logAdapters.forEach {
            if (it.isLoggable(priority, myTag)) {
                return true
            }
        }
        return false
    }

}
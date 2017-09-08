package com.threshold.prettylogger

import android.app.Application
import com.threshold.logger.*
import com.threshold.logger.adapter.AndroidLogAdapter
import com.threshold.logger.strategy.PrettyFormatStrategy

class App : Application(), PrettyLogger {

    override fun onCreate() {
        super.onCreate()
        initPrettyLogger()
        verbose("Verbose")
        warn("Warn")
        info { "Info" }
        debug { "Debug" }
        error("error message", Throwable("Custom throwable info"))
        tag(loggerTag).wtf("WTF")
        tag("OnceOnlyTag")
                .debugJson {
                    "{\n" +
                            "    \"name\": \"Jane\", \n" +
                            "    \"age\": 20\n" +
                            "}"
                }
        debugXml {
            "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n" +
                    "<note>\n" +
                    "    <to>George</to>\n" +
                    "    <from>John</from>\n" +
                    "    <heading>Reminder</heading>\n" +
                    "    <body>Don't forget the meeting!</body>\n" +
                    "</note>"
        }
        verbose(null)
        tag("DebugNullOnceOnlyTag").debug { null }
        info { null }
        tag("ErrorNullOnceOnlyTag").error(null)
        warn(null)
        debugJson(null)
        debugXml { null }

        debug(listOf(1, 2, 3, 4, 5))
        info(arrayListOf("hello", "world"))
        warn(mapOf(1 to "Beijing", 2 to "Shanghai", 3 to "Nanjing", 4 to "Chongqing"))
        verbose("{ \"key\": 3, \n \"value\": something}")
        error(booleanArrayOf(true, false, false, true))
        info { byteArrayOf(-128, 0, 1, 2, 3, 4, 5, 0xA, 0xB, 0xf, 16, 17, 127) }
        verbose(doubleArrayOf(1.20, 1.33, 1.55, 2.11))
        info(floatArrayOf(1.11f, 2.34f, 5.34f))
        warn(User("Jane", 25, "Beijing"))
    }

    private fun initPrettyLogger() {
        val prettyFormatStrategy = PrettyFormatStrategy.build {
            showThreadInfo = false  // (Optional) Whether to show thread info or not. Default true
            methodCount = 3         // (Optional) How many method line to show. Default 2
            methodOffset = 0        // (Optional) Hides internal method calls up to offset. Default 0
//            logStrategy = customLog // (Optional) Changes the log strategy to print out. Default LogCat
            tag = "Threshold"   // (Optional) Global tag for every log. Default PRETTY_LOGGER
        }
        addAdapter(object : AndroidLogAdapter(prettyFormatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG && super.isLoggable(priority, tag)
            }
        })
    }

    data class User(val name: String, val age: Int, var address: String)
}
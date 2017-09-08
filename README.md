# PrettyLogger
[![](https://jitpack.io/v/wind0ws/prettylogger.svg)](https://jitpack.io/#wind0ws/prettylogger)

>中文说明，请点[这里](http://www.jianshu.com/p/fd0f390ec775)查看.

This project is a kotlin version of orhanobt's [logger](https://github.com/orhanobut/logger) and inspiration by [AnkoLogger](https://github.com/Kotlin/anko/wiki/Anko-Commons-%E2%80%93-Logging), I just made some small changes and bring some small new features.

>If your project is not integration with kotlin, I recommend you use orhanobut's [logger](https://github.com/orhanobut/logger)

## [Getting started](https://jitpack.io/#wind0ws/prettylogger)
The first step is to include Pretty Logger into your project, for example, as a Gradle compile dependency:
* Because of using [jitpack.io](https://jitpack.io/), so we need add the jitpack.io repository in your root project gradle:
```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
* and then add pretty logger dependency in your module gradle:
```groovy
dependencies {
			...
	        implemention 'com.github.wind0ws:prettylogger:x.y.z'
	}
```
> If your gradle version below 3.0, just replace `implemention` keyword to `compile`.
> Note: you need replace `x.y.z` to the correct version, you can find it on [release page](https://github.com/wind0ws/prettylogger/releases)

All right, we are done for integration.

## Hello,World.
PrettyLogger need config at least once before use, you can config it at anywhere. generally speaking, we config it at `Application` `onCreate`

The simplest demo:
```Kotlin
class App : Application(), PrettyLogger {

    override fun onCreate() {
        super.onCreate()
        addAdapter(AndroidLogAdapter() )
        debug { "Hello World" }
    }

}
```

If you want to custom output of log, such as TAG, stop output log in release build..., you can config `FormatStrategy`,for example:
```Kotlin
class App : Application(), PrettyLogger {

    override fun onCreate() {
        super.onCreate()
        initPrettyLogger ()

         debug { "Hello World" }
    }

    private fun initPrettyLogger () {
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
}
```

### Common Usage：
```Kotlin
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
```

Each method has two versions: plain and lazy
```Kotlin
        info("String " + "concatenation")
        info { "String " + "concatenation" }
```
Lambda result will be calculated only if `LogAdapter.isLoggable(Log.INFO, tag)` is `true`.

### Want demo?
See app module in this repo.
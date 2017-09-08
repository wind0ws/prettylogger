package com.threshold.logger.adapter

import com.threshold.logger.strategy.FormatStrategy
import com.threshold.logger.strategy.PrettyFormatStrategy

open class AndroidLogAdapter(private val formatStrategy: FormatStrategy) : LogAdapter {

    constructor() : this(PrettyFormatStrategy.Builder().build())

    override fun isLoggable(priority: Int, tag: String?): Boolean {
        return true
    }

    override fun log(priority: Int, tag: String?, message: String?) {
        formatStrategy.log(priority, tag, message)
    }
}
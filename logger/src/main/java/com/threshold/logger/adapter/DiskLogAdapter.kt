package com.threshold.logger.adapter

import com.threshold.logger.strategy.CsvFormatStrategy
import com.threshold.logger.strategy.FormatStrategy

open class DiskLogAdapter(private val formatStrategy: FormatStrategy) : LogAdapter {

    constructor() : this(CsvFormatStrategy.Builder().build())

    override fun isLoggable(priority: Int, tag: String?): Boolean {
        return true
    }

    override fun log(priority: Int, tag: String?, message: String?) {
        formatStrategy.log(priority, tag, message)
    }
}
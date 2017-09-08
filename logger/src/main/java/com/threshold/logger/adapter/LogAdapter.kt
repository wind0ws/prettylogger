package com.threshold.logger.adapter

import com.threshold.logger.strategy.LogStrategy

interface LogAdapter : LogStrategy {
    fun isLoggable(priority: Int, tag: String?): Boolean
}
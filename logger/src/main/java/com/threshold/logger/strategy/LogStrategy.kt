package com.threshold.logger.strategy

interface LogStrategy {
    fun log(priority: Int, tag: String?, message: String?)
}
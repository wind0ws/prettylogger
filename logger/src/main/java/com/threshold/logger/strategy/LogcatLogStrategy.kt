package com.threshold.logger.strategy

class LogcatLogStrategy : LogStrategy {
    override fun log(priority: Int, tag: String?, message: String?) {
        android.util.Log.println(priority, tag, message)
    }
}
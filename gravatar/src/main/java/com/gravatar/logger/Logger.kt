package com.gravatar.logger

import android.util.Log

class Logger {
    fun i(tag: String, message: String) = Log.i(tag, message)
    fun w(tag: String, message: String) = Log.w(tag, message)
    fun e(tag: String, message: String) = Log.e(tag, message)
}

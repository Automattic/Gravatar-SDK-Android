package com.gravatar.events.ui.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.findAndroidActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
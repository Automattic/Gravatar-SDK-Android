package com.gravatar.quickeditor.initializer

import android.content.Context
import androidx.startup.Initializer
import com.gravatar.quickeditor.QuickEditorContainer

internal class QuickEditorInitializer : Initializer<QuickEditorContainer> {
    override fun create(context: Context): QuickEditorContainer {
        return QuickEditorContainer.init(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}

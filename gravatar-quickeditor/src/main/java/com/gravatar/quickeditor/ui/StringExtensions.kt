package com.gravatar.quickeditor.ui

internal val String.removeOrphans: String
    get() {
        val space = " "
        val index = lastIndexOf(space, ignoreCase = true)
        return if (index < 0) this else this.replaceRange(index, index + space.length, "\u00A0")
    }

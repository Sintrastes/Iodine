package com.bedelln.composetk.desktop.ctx

import androidx.compose.ui.window.Notifier

interface SystemCtx {
    val notifier: Notifier
}

fun SystemCtx.notify(title: String, message: String) {
    notifier.notify(title, message)
}
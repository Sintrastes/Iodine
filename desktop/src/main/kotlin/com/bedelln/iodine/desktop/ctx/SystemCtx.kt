package com.bedelln.iodine.desktop.ctx

import androidx.compose.ui.window.Notifier
import com.bedelln.iodine.IodineContext

interface SystemCtx: IodineContext {
    val notifier: Notifier
}

fun SystemCtx.notify(title: String, message: String) {
    notifier.notify(title, message)
}
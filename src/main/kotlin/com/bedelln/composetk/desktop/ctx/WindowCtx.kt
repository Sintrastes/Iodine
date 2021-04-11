package com.bedelln.composetk.desktop.ctx

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Notifier
import kotlinx.coroutines.CoroutineScope

interface WindowRef {
    fun addToContents(f: @Composable() () -> Unit)
}

interface WindowCtx: SystemCtx {
    val window: WindowRef
    val windowScope: CoroutineScope
}
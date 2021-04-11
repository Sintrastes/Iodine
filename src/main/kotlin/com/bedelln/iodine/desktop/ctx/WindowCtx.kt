package com.bedelln.iodine.desktop.ctx

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

interface WindowRef {
    fun addToContents(f: @Composable() () -> Unit)
}

interface WindowCtx: SystemCtx {
    val window: WindowRef
    val windowScope: CoroutineScope
}
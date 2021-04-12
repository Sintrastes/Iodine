package com.bedelln.iodine.desktop.ctx

import androidx.compose.runtime.Composable
import com.bedelln.iodine.ContainerRef
import kotlinx.coroutines.CoroutineScope

/** Reference to an OS window. */
interface WindowRef: ContainerRef

interface WindowCtx: SystemCtx {
    val window: WindowRef
    val windowScope: CoroutineScope
}
package com.bedelln.iodine.desktop.ctx

import androidx.compose.runtime.Composable
import com.bedelln.iodine.ContainerRef
import com.bedelln.iodine.HasRef
import kotlinx.coroutines.CoroutineScope

/** Reference to an OS window. */
interface WindowRef: ContainerRef

interface WindowCtx: SystemCtx, HasRef {
    val window: WindowRef
    val windowScope: CoroutineScope
}
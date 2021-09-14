package com.bedelln.iodine.desktop.ctx

import com.bedelln.iodine.interfaces.ContainerRef
import com.bedelln.iodine.interfaces.HasRef
import kotlinx.coroutines.CoroutineScope

/** Reference to an OS window. */
interface WindowRef: ContainerRef

interface WindowCtx: SystemCtx, HasRef {
    val window: WindowRef
    val windowScope: CoroutineScope
}
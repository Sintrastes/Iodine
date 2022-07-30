package com.bedelln.iodine.desktop.ctx

import kotlinx.coroutines.CoroutineScope

/**
 * Iodine context for a component displayed in the context of
 *  a window.
 */
interface WindowCtx: SystemCtx {
    val windowScope: CoroutineScope
}
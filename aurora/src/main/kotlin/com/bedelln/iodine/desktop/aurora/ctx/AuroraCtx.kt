package com.bedelln.iodine.desktop.aurora.ctx

import com.bedelln.iodine.interfaces.IodineContext
import org.pushingpixels.aurora.window.AuroraApplicationScope

interface AuroraCtx: IodineContext {
    val scope: AuroraApplicationScope
}
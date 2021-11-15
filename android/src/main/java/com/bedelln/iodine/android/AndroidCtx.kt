package com.bedelln.iodine.android

import com.bedelln.iodine.interfaces.IodineContext
import android.content.Context

/**
 * Minimal [IodineContext] for an Android application.
 */
interface AndroidCtx: IodineContext {
    val defaultCtx: Context
}
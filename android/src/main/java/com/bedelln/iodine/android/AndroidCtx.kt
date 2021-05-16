package com.bedelln.iodine.android

import com.bedelln.iodine.interfaces.IodineContext
import android.content.Context

interface AndroidCtx: IodineContext {
    val defaultCtx: Context
}
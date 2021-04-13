package com.bedelln.iodine.android

import com.bedelln.iodine.IodineContext
import android.content.Context

interface AndroidCtx: IodineContext {
    val defaultCtx: Context
}
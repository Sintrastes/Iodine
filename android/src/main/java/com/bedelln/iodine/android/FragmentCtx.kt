package com.bedelln.iodine.android

import android.content.Context
import com.bedelln.iodine.HasRef

interface FragmentCtx: AndroidCtx, HasRef {
    val fragmentCtx: Context
}
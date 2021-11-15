package com.bedelln.iodine.android

import android.content.Context
import com.bedelln.iodine.interfaces.HasRef

/**
 * Iodine context for a component living inside an Android fragment.
 */
interface FragmentCtx: AndroidCtx, HasRef {
    val fragmentCtx: Context
}